package controller;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
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
}
