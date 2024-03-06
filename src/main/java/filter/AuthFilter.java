package filter;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
import entity.Session;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthorizationService;
import service.encryption.EncryptionService;
import utility.PropertiesUtility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AuthFilter implements Filter {
    private static final String COOKIE_NAME = "acc";
    private static final String SESSION_UUID_ATTRIBUTE = "session_uuid";
    private static final String SESSION_USER_ATTRIBUTE = "user";
    private static final String SESSION_EXPIRES_AT_ATTRIBUTE = "expires_at";
    private AuthorizationService authorizationService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.authorizationService = buildEncryptionService();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        authorizationCheck(servletRequest, servletResponse, filterChain);
    }

    private void authorizationCheck(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        final var req = (HttpServletRequest) servletRequest;
        final var resp = (HttpServletResponse) servletResponse;

        try{
            var optionalCookie = usersCookieCheck(req);

            if(optionalCookie.isEmpty()){
                setRequiredAttributes(req.getSession(), null, null, null);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            var cookie = optionalCookie.get();

            var optionalSession = getUserSession(cookie.getValue(), req);

            if(optionalSession.isEmpty()){
                cookie.setMaxAge(0);
                resp.addCookie(cookie);

                setRequiredAttributes(req.getSession(), null, null, null);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            var session = optionalSession.get();

            setRequiredAttributes(req.getSession(), session.getUuid(), session.getUser(), session.getExpiresAt());
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception ex){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Optional<Cookie> usersCookieCheck(HttpServletRequest req){
        final var cookies = req.getCookies();

        if(cookies == null)
            return Optional.empty();

        return Arrays.stream(req.getCookies()).filter(x -> x.getName().equals(COOKIE_NAME)).findFirst();
    }

    private Optional<Session> getUserSession(String userId, HttpServletRequest req){
        var sessionFromHttp = seeCurrentSession(req);

        if(sessionFromHttp.isPresent()){
            return sessionFromHttp;
        }else{
            if(userId == null)
                return Optional.empty();

            Long id = Long.parseLong(userId);
            return this.authorizationService.findSessionByUserId(id);
        }
    }

    private Optional<Session> seeCurrentSession(HttpServletRequest req){
        var httpSession = req.getSession();

        var uuid = httpSession.getAttribute(SESSION_UUID_ATTRIBUTE);
        var user = httpSession.getAttribute(SESSION_USER_ATTRIBUTE);
        var expiresAt = httpSession.getAttribute(SESSION_EXPIRES_AT_ATTRIBUTE);

        var hasRequiredAttributes =
                uuid != null ||
                user != null ||
                expiresAt != null;

        if(hasRequiredAttributes){
            Session session = new Session();
            session.setUuid((UUID) uuid);
            session.setUser((User) user);
            session.setExpiresAt((Timestamp) expiresAt);

            return Optional.of(session);
        }

        return Optional.empty();
    }

    private void setRequiredAttributes(HttpSession httpSession, UUID uuid, User user, Timestamp expireTime){
        httpSession.setAttribute(SESSION_UUID_ATTRIBUTE, uuid);
        httpSession.setAttribute(SESSION_USER_ATTRIBUTE, user);
        httpSession.setAttribute(SESSION_EXPIRES_AT_ATTRIBUTE, expireTime);
    }

    private AuthorizationService buildEncryptionService(){
        final var userDAO = new UserRepository();
        final var sessionDAO = new SessionRepository();
        final var encryptionMethod = createEncryptionService();

        return new AuthorizationService(userDAO, sessionDAO, encryptionMethod);
    }

    private EncryptionService createEncryptionService(){
        EncryptionService service;

        String encryptionMethod = PropertiesUtility.getApplicationProperty("app.encryption_method");
        try {
            service = (EncryptionService) Class.forName(encryptionMethod).getDeclaredConstructor().newInstance();
        } catch (
                ClassNotFoundException    |
                InvocationTargetException |
                InstantiationException    |
                IllegalAccessException    |
                NoSuchMethodException e
        ) {
            throw new RuntimeException(e);
        }

        return service;
    }
}
