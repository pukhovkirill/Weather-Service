package test;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthorizationService;
import service.encryption.AESEncryptionService;


public class AuthorizationServiceTest {
    private AuthorizationService authorizationService;

    @BeforeEach
    void init(){
        var userRepository = new UserRepository();
        var sessionRepository = new SessionRepository();
        var encryptionService = new AESEncryptionService();

        authorizationService = new AuthorizationService(userRepository, sessionRepository, encryptionService);
    }

    @Test
    void registrationTest(){
        String testLogin = "login@test.com";
        String password = "password";

        authorizationService.registration(testLogin, password);
    }

    @Test
    void authorizationTest(){

    }

    @Test
    void findSessionTest(){

    }

    @Test
    void logout(){

    }
}
