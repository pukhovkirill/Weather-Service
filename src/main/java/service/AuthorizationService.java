package service;

import dao.SessionDAO;
import dao.UserDAO;
import entity.Session;
import entity.User;
import service.encryption.EncryptionService;
import utility.PropertiesUtility;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

public class AuthorizationService {
    private final UserDAO userRepository;
    private final SessionDAO sessionRepository;
    private final EncryptionService encryptionService;
    private final Long sessionLifetime;

    public AuthorizationService(UserDAO userRepository, SessionDAO sessionRepository, EncryptionService encryptionService){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.encryptionService = encryptionService;
        this.sessionLifetime = Long.valueOf(PropertiesUtility.getApplicationProperty("app.session_lifetime"));
    }

    public boolean registration(String login, String password){
        if(userRepository.findByLogin(login).isPresent())
            return false;

        var publicKey = encryptionService.generateKey();
        var salt = encryptionService.hash(publicKey);

        User user = new User();
        user.setLogin(login);

        var hashedPassword = encryptionService.encrypt(password, publicKey, salt);
        user.setPassword(hashedPassword);
        user.setPublicKey(publicKey);

        user.setLocations(new LinkedList<>());

        userRepository.save(user);

        return true;
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

        var timestamp = new Timestamp(System.currentTimeMillis() + sessionLifetime);
        session.setExpiresAt(timestamp);

        this.sessionRepository.save(session);

        return session;
    }

    public Optional<Session> findSessionByUUID(UUID uuid){
        var optionalSession = sessionRepository.find(uuid);

        if(optionalSession.isEmpty())
            return Optional.empty();

        var session = optionalSession.get();

        var expiresAt = session.getExpiresAt();
        var current = new Timestamp(System.currentTimeMillis());

        if (current.compareTo(expiresAt) > 0) {
            sessionRepository.delete(session.getUuid());
            return Optional.empty();
        }

        return optionalSession;
    }

    private Optional<Session> findSession(UUID uuid){
        var optionalSession = sessionRepository.find(uuid);

        if(optionalSession.isEmpty())
            return Optional.empty();

        var session = optionalSession.get();

        var expiresAt = session.getExpiresAt();
        var current = new Timestamp(System.currentTimeMillis());

        if (current.compareTo(expiresAt) > 0) {
            sessionRepository.delete(session.getUuid());
            return Optional.empty();
        }

        return optionalSession;
    }

    public boolean logout(UUID uuid){
        var session = findSession(uuid);

        if(session.isEmpty())
            return false;

        sessionRepository.delete(session.get().getUuid());
        return true;
    }
}
