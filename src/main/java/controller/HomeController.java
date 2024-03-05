package controller;

import entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import java.io.Writer;

public class HomeController implements MappingController{

    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        var session = req.getSession();

        var user = (User) session.getAttribute("user");

        ctx.setVariable("user", user);
        templateEngine.process("index", ctx, writer);
    }
}
