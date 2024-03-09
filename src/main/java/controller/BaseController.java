package controller;

import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.context.WebContext;

public abstract class BaseController implements MappingController{
    protected static final String IS_USER_AUTHORIZED_VARIABLE = "is_user_authorized";

    public BaseController(){}

    protected void setVariables(WebContext ctx, boolean isUserAuthorized){
        ctx.setVariable(IS_USER_AUTHORIZED_VARIABLE, isUserAuthorized);
    }

    protected boolean isUserAuthorized(HttpServletRequest req){
        var session = req.getSession();
        return session.getAttribute("user") != null;
    }
}
