package service;

import dao.SessionDAO;
import dao.UserDAO;
import entity.Session;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import service.encryption.EncryptionService;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthorizationServiceTest {
    private AuthorizationService authorizationService;
    private User testUser;
    private Session testSession;
    private static final String testLogin = "login@test.com";
    private static final String testPassword = "password";
    private static final String passwordEncrypt = "RvKcmbgYs6et9YSOnN4jYA==";
    private static final String publicKey = "c6292773603d0d6aabbdd62a11ef721d1542d8";
    private static final String hash = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
    private static final UUID sessionUUID = UUID.fromString("b65deb55-c667-464f-a7da-c781bcdedc5d");

    private UserDAO userRepository;
    private SessionDAO sessionRepository;
    private EncryptionService encryptionService;

    @BeforeEach
    void init(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin(testPassword);
        testUser.setPublicKey(publicKey);
        testUser.setPassword(passwordEncrypt);
        testUser.setLocations(new LinkedList<>());

        testSession = new Session();
        testSession.setUuid(sessionUUID);
        testSession.setUser(testUser);
        testSession.setExpiresAt(new Timestamp(System.currentTimeMillis()+2592000000L));

        userRepository = Mockito.mock(UserDAO.class);
        sessionRepository = Mockito.mock(SessionDAO.class);

        encryptionService = Mockito.mock(EncryptionService.class);
        Mockito.when(encryptionService.generateKey()).thenReturn(publicKey);
        Mockito.when(encryptionService.hash(publicKey)).thenReturn(hash);
        Mockito.when(encryptionService.encrypt(testPassword, publicKey, hash)).thenReturn(passwordEncrypt);
        Mockito.when(encryptionService.decrypt(passwordEncrypt, publicKey, hash)).thenReturn(testPassword);
    }

    void initForRegistration(){
        Mockito.when(userRepository.findByLogin(testLogin)).thenReturn(Optional.empty());
        Mockito.when(userRepository.find(1L)).thenReturn(Optional.empty());

        authorizationService = new AuthorizationService(userRepository, sessionRepository, encryptionService);
    }

    @Test
    @Order(1)
    void registrationTest(){
        initForRegistration();
        var result = authorizationService.registration(testLogin, testPassword);
        assertTrue(result);
    }

    void initForAuthorization(){
        Mockito.when(userRepository.findByLogin(testLogin)).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.find(1L)).thenReturn(Optional.of(testUser));

        Mockito.when(sessionRepository.findByUser(testUser)).thenReturn(Optional.of(testSession));
        Mockito.when(sessionRepository.find(sessionUUID)).thenReturn(Optional.of(testSession));

        authorizationService = new AuthorizationService(userRepository, sessionRepository, encryptionService);
    }

    @Test
    @Order(2)
    void authorizationTest(){
        initForAuthorization();
        var optionalSession = authorizationService.authorization(testLogin, testPassword);
        assertTrue(optionalSession.isPresent());
    }

    void initForFindSession(){
        Mockito.when(userRepository.findByLogin(testLogin)).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.find(1L)).thenReturn(Optional.of(testUser));

        Mockito.when(sessionRepository.findByUser(testUser)).thenReturn(Optional.of(testSession));
        Mockito.when(sessionRepository.find(sessionUUID)).thenReturn(Optional.of(testSession));

        authorizationService = new AuthorizationService(userRepository, sessionRepository, encryptionService);
    }

    @Test
    @Order(3)
    void findSessionTest(){
        initForFindSession();
        var optionalSession = authorizationService.findSessionByUUID(sessionUUID);
        assertEquals(testSession, optionalSession.get());
    }

    void initForLogout(){
        Mockito.when(userRepository.findByLogin(testLogin)).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.find(1L)).thenReturn(Optional.of(testUser));

        Mockito.when(sessionRepository.findByUser(testUser)).thenReturn(Optional.of(testSession));
        Mockito.when(sessionRepository.find(sessionUUID)).thenReturn(Optional.of(testSession));

        authorizationService = new AuthorizationService(userRepository, sessionRepository, encryptionService);
    }
    @Test
    @Order(4)
    void logoutTest(){
        initForLogout();
        assertTrue(authorizationService.logout(sessionUUID));
    }
}
