package service.encryption;

public interface EncryptionService {
    String encrypt(String plaintext, String key);
    String decrypt(String ciphertext, String key);
    boolean regenerateKey();
    String getKeyAlias();
    void setKeyLength(int keyLength);
    String hash(String input);
    boolean validateKey(String key);
}
