package controller;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.context.WebContext;
import service.AuthorizationService;
import service.encryption.AESEncryptionService;

public abstract class AuthBaseController implements MappingController{
    protected final AuthorizationService authorizationService;

    public AuthBaseController(){
        final var userDao = new UserRepository();
        final var sessionDao = new SessionRepository();
        final var encryptionMethod = new AESEncryptionService();

        this.authorizationService = new AuthorizationService(userDao, sessionDao, encryptionMethod);
    }

    protected void errorCheck(WebContext ctx, HttpServletRequest req){
        var auth_error = req.getSession().getAttribute("auth_error");
        req.getSession().removeAttribute("auth_error");
        ctx.setVariable("error_message", auth_error);
        ctx.setVariable("is_auth_error", auth_error==null);
    }

    protected  void setError(HttpServletRequest req, String message){
        req.getSession().setAttribute("auth_error", message);
    }
}
