package controller;

import entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

public class LogoutController extends AuthBaseController{
    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var httpSession = req.getSession();

        var user = (User) httpSession.getAttribute("user");

        if(user == null)
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");

        if(authorizationService.logout(user)){
            httpSession.removeAttribute("uuid");
            httpSession.removeAttribute("user");
            httpSession.removeAttribute("expires_at");

            var optionalCookie = Arrays.stream(req.getCookies()).filter(x -> x.getName().equals("acc")).findFirst();

            if(optionalCookie.isPresent()){
                var cookie = optionalCookie.get();
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
        }
        resp.sendRedirect("/");
    }
}
