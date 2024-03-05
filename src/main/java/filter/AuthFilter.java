package filter;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AuthorizationService;
import service.encryption.AESEncryptionService;

import java.io.IOException;
import java.util.Arrays;

public class AuthFilter implements Filter {

    private AuthorizationService authorizationService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final var userDAO = new UserRepository();
        final var sessionDAO = new SessionRepository();
        final var encryptionMethod = new AESEncryptionService();
        this.authorizationService = new AuthorizationService(userDAO, sessionDAO, encryptionMethod);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final var req = (HttpServletRequest) servletRequest;
        final var resp = (HttpServletResponse) servletResponse;
        String url = req.getRequestURI();

        try{
            final var httpSession = req.getSession();
            var user = (User) httpSession.getAttribute("user");

            if(user == null){
                var optionalCookie = Arrays.stream(req.getCookies()).filter(x -> x.getName().equals("acc")).findFirst();

                if(optionalCookie.isEmpty()){
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }

                final var cookie = optionalCookie.get();

                Long userId = Long.parseLong(cookie.getAttribute("user_id"));

                var optionalSession = this.authorizationService.findSessionByUserId(userId);

                if(optionalSession.isEmpty()){
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }

                var session = optionalSession.get();

                httpSession.setAttribute("uuid", session.getUuid());
                httpSession.setAttribute("user", session.getUser());
                httpSession.setAttribute("expires_at", session.getExpiresAt());
            }

            if(httpSession.getAttribute("user") != null)
                if(url.startsWith("/login") || url.startsWith("/registration"))
                    throw new Exception();

            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception ex){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
