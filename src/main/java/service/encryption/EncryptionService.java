package service.encryption;

import java.security.*;
import java.util.Base64;

public interface EncryptionService {
    String encrypt(String plaintext, String key, String salt);
    String decrypt(String ciphertext, String key, String salt);
    default String generateKey(){
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom secureRandom = new SecureRandom();

            keyPairGenerator.initialize(1024, secureRandom);

            var keyPair = keyPairGenerator.generateKeyPair();

            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            var publicKey = Base64.getEncoder().encodeToString(publicKeyBytes);

            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
            var privateKey = Base64.getEncoder().encodeToString(privateKeyBytes);

            KeyStorage.storingIntoKeyStore(privateKey, publicKey);

            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    void setKeyLength(int keyLength);
    int getKeyLength();
    String hash(String input);
}
