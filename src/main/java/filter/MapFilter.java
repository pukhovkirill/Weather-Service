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

import java.io.*;

public class MapFilter implements Filter {

    private ITemplateEngine templateEngine;
    private JakartaServletWebApplication application;

    @Override
    public void init(final FilterConfig filterConfig) {
        this.application =
                JakartaServletWebApplication.buildApplication(filterConfig.getServletContext());
        this.templateEngine = buildTemplateEngine(this.application);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        gettingStaticResources((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        process((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
    }

    private void process(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
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

        if(req.getSession().getAttribute("user") == null){
            if(uri.startsWith("/search") || uri.startsWith("/forecast")){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return true;
            }
        }
            
        return false;
    }


    private void gettingStaticResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       var uri =  req.getRequestURI();
       var uriParts = uri.split("/");

       if(uri.contains("/img")){
           resp.setContentType("text/html; charset=UTF-8");
           resp.setHeader("Pragma", "no-cache");
           resp.setHeader("Cache-Control", "no-cache");
           resp.setDateHeader("Expires", 0);

           final var context = req.getServletContext();
           var fileUri = MapFilter.class.getResource("/img/"+uriParts[uriParts.length-1]);
           if(fileUri == null)
               return;

           var imagePath = fileUri.getPath();
           var mime = context.getMimeType(imagePath);
           if(mime == null)
               return;

           resp.setContentType(mime);
           File file = new File(imagePath);
           resp.setContentLength((int)file.length());

           FileInputStream in = new FileInputStream(file);
           OutputStream out = resp.getOutputStream();

           byte[] buf = new byte[1024];
           int count;
           while((count = in.read(buf)) >= 0){
               out.write(buf, 0, count);
           }
           out.close();
           in.close();
       }
    }

    private void setRequiredHeaders(HttpServletResponse resp){
        resp.setContentType("text/html; charset=UTF-8");
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
