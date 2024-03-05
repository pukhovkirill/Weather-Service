package filter;

import controller.MappingController;
import controller.ThymeleafTemplateEngine;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapping.ControllerMappings;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

public class MapFilter implements Filter {

    private ITemplateEngine templateEngine;
    private JakartaServletWebApplication application;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.application =
                JakartaServletWebApplication.buildApplication(filterConfig.getServletContext());
        this.templateEngine = buildTemplateEngine(this.application);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        process((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
    }

    private void process(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try{
            final var webExchange = this.application.buildExchange(request, response);
            final var webRequest = webExchange.getRequest();

            if (
                    webRequest.getPathWithinApplication().startsWith("/img") ||
                    webRequest.getPathWithinApplication().startsWith("/icon")||
                    webRequest.getPathWithinApplication().startsWith("/template")
            ){
                return;
            }

            final var controller = ControllerMappings.resolveControllerForRequest(webRequest);
            if (controller == null)
                return;

            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);


            final var writer = response.getWriter();

            final var thymeleafEngine = new ThymeleafTemplateEngine(webExchange, this.templateEngine, writer);
            executeHttpMethod(controller, thymeleafEngine, request, response);
        }catch (Exception ex){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void executeHttpMethod(MappingController controller, ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        final var method = engine.getWebExchange().getRequest().getMethod();

        switch (method.toLowerCase()){
            case "get" -> controller.processGet(engine, req, resp);
            case "head" -> controller.processHead(engine, req, resp);
            case "post" -> controller.processPost(engine, req, resp);
            case "put" -> controller.processPut(engine, req, resp);
            case "delete" -> controller.processDelete(engine, req, resp);
            case "connect" -> controller.processConnect(engine, req, resp);
            case "options" -> controller.processOptions(engine, req, resp);
            case "trace" -> controller.processTrace(engine, req, resp);
            case "patch" -> controller.processPatch(engine, req, resp);
        }
    }

    private static ITemplateEngine buildTemplateEngine(final IWebApplication application) {

        final var templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);

        templateResolver.setPrefix("/views/");
        templateResolver.setSuffix(".html");

        templateResolver.setCacheTTLMs(3600000L);

        templateResolver.setCacheable(true);

        final var templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }
}
