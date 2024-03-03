package test;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.AuthorizationService;
import service.encryption.AESEncryptionService;

import static org.junit.jupiter.api.Assertions.assertTrue;


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
    @Order(1)
    void registrationTest(){
        String testLogin = "login@test.com";
        String password = "password";

        authorizationService.registration(testLogin, password);
    }

    @Test
    @Order(2)
    void authorizationTest(){
        String testLogin = "login@test.com";
        String password = "password";

        var optionalUser = authorizationService.authorization(testLogin, password);
        assertTrue(optionalUser.isPresent());
    }

    @Test
    @Order(3)
    void findSessionTest(){

    }

    @Test
    @Order(4)
    void logout(){

    }
}
