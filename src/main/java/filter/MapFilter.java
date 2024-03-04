package filter;

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
        if (!process((HttpServletRequest)servletRequest, (HttpServletResponse)servletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean process(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try{
            final var webExchange = this.application.buildExchange(request, response);
            final var webRequest = webExchange.getRequest();

            if (
                    webRequest.getPathWithinApplication().startsWith("/css") ||
                    webRequest.getPathWithinApplication().startsWith("/img") ||
                    webRequest.getPathWithinApplication().startsWith("/js")  ||
                    webRequest.getPathWithinApplication().startsWith("/icon")
            ){
                return false;
            }


            final var controller = ControllerMappings.resolveControllerForRequest(webRequest);
            if (controller == null) {
                return false;
            }

            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);


            final var writer = response.getWriter();

            controller.process(webExchange, this.templateEngine, writer);

            return true;
        }catch (Exception ex){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return false;
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
