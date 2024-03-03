package service;

import dao.SessionDAO;
import dao.UserDAO;
import entity.Session;
import entity.User;
import service.encryption.EncryptionService;
import utility.PropertiesUtility;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;

public class AuthorizationService {
    private final UserDAO userRepository;
    private final SessionDAO sessionRepository;
    private final EncryptionService encryptionService;
    private final Long cookieLifetime;

    public AuthorizationService(UserDAO userRepository, SessionDAO sessionRepository, EncryptionService encryptionService){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.encryptionService = encryptionService;
        this.cookieLifetime = Long.valueOf(PropertiesUtility.getApplicationProperty("app.cookie_lifetime"));
    }

    public void registration(String login, String password){
        var publicKey = encryptionService.generateKey();
        var salt = encryptionService.hash(publicKey);

        User user = new User();
        user.setLogin(login);

        var hashedPassword = encryptionService.encrypt(password, publicKey, salt);
        user.setPassword(hashedPassword);
        user.setPublicKey(publicKey);

        user.setLocations(new HashSet<>());

        userRepository.save(user);
    }

    public Optional<Session> authorization(String login, String password){
        var optionalUser = userRepository.findByLogin(login);

        if(optionalUser.isEmpty())
            return Optional.empty();

        var user = optionalUser.get();

        var publicKey = user.getPublicKey();
        var salt = encryptionService.hash(publicKey);

        var hashedPassword = user.getPassword();
        var unhashedPassword = encryptionService.decrypt(hashedPassword, publicKey, salt);

        if(!unhashedPassword.equals(password))
            return Optional.empty();

        return Optional.of(createSession(user));
    }

    private Session createSession(User user){
        var session = new Session();

        session.setUser(user);

        var timestamp = new Timestamp(System.currentTimeMillis() + cookieLifetime);
        session.setExpiresAt(timestamp);

        this.sessionRepository.save(session);

        return session;
    }

    public Optional<Session> findSession(User user){
        var optionalSession = sessionRepository.findByUser(user);

        if(optionalSession.isEmpty())
            return Optional.empty();

        var session = optionalSession.get();

        var expiresAt = session.getExpiresAt();
        var current = new Timestamp(System.currentTimeMillis());

        if (current.compareTo(expiresAt) > 0) {
            sessionRepository.delete(session.getId());
            return Optional.empty();
        }

        return optionalSession;
    }

    public boolean logout(User user){
        var session = findSession(user);

        if(session.isEmpty())
            return false;

        sessionRepository.delete(session.get().getId());
        return true;
    }
}
