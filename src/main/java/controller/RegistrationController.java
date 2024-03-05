package controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;

public class RegistrationController extends AuthBaseController{

    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        templateEngine.process("registration", ctx, writer);
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        var email = req.getParameter("email");
        var password = req.getParameter("password");

        authorizationService.registration(email, password);
        resp.sendRedirect("/login");
    }

}
