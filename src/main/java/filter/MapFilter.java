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
            if(isForbiddenUri(request, response))
                return;

            final var webExchange = this.application.buildExchange(request, response);
            final var webRequest = webExchange.getRequest();

            final var controller = ControllerMappings.resolveControllerForRequest(webRequest);
            if (controller == null)
                return;

            setRequiredHeaders(response);

            final var writer = response.getWriter();
            final var thymeleafEngine = new ThymeleafTemplateEngine(webExchange, this.templateEngine, writer);
            executeHttpMethod(controller, thymeleafEngine, request, response);
        }catch (Exception ex){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isForbiddenUri(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var uri = req.getRequestURI();

        if(req.getSession().getAttribute("user") != null){
            if(uri.startsWith("/login") || uri.startsWith("/registration")){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return true;
            }
        }

        if(uri.startsWith("/img") || uri.startsWith("/icon")|| uri.startsWith("/template"))
            return true;
            
        return false;
    }

    private void setRequiredHeaders(HttpServletResponse resp){
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
    }

    private void executeHttpMethod(MappingController controller, ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        final var method = engine.getWebExchange().getRequest().getMethod();

        switch (method.toLowerCase()){
            case "get" -> controller.processGet(engine, req, resp);
            case "post" -> controller.processPost(engine, req, resp);
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
