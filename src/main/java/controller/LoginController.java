package controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;

public class LoginController extends AuthBaseController{

    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        templateEngine.process("login", ctx, writer);
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        var email = req.getParameter("email");
        var password = req.getParameter("password");

        var optionalSession = authorizationService.authorization(email, password);

        if(optionalSession.isEmpty())
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);

        var session = optionalSession.get();

        var httpSession = req.getSession();

        httpSession.setAttribute("uuid", session.getUuid());
        httpSession.setAttribute("user", session.getUser());
        httpSession.setAttribute("expires_at", session.getExpiresAt());

        var cookie = new Cookie("acc", String.valueOf(session.getUser().getId()));

        resp.addCookie(cookie);

        resp.sendRedirect("/");
    }
}
