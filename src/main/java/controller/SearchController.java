package controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;


public class SearchController implements MappingController{
    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var location = req.getParameter("wanted");

        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());

        templateEngine.process("search", ctx, writer);
    }
}
