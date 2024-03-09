package controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import utility.PropertiesUtility;

public class LoginController extends AuthBaseController{
    private static final int COOKIE_LIFETIME =
            Integer.parseInt(PropertiesUtility.getApplicationProperty("app.cookie_lifetime"));

    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) {
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        errorCheck(ctx, req);

        templateEngine.process("login", ctx, writer);
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        var email = req.getParameter("email");
        var password = req.getParameter("password");

        var optionalSession = authorizationService.authorization(email, password);

        if(optionalSession.isEmpty()){
            setError(req, "Incorrect login or password. Please try again");
            resp.sendRedirect("/login");
            return;
        }

        var session = optionalSession.get();

        var httpSession = req.getSession();

        httpSession.setAttribute("uuid", session.getUuid());
        httpSession.setAttribute("user", session.getUser());
        httpSession.setAttribute("expires_at", session.getExpiresAt());

        var cookie = new Cookie("acc", session.getUuid().toString());
        cookie.setMaxAge(COOKIE_LIFETIME);

        resp.addCookie(cookie);

        resp.sendRedirect("/");
    }
}
