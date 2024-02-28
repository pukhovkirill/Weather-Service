package service.encryption;

public class BCryptEncryptionService implements EncryptionService{
    @Override
    public String encrypt(String plaintext, String key) {
        return null;
    }

    @Override
    public String decrypt(String ciphertext, String key) {
        return null;
    }

    @Override
    public boolean regenerateKey() {
        return false;
    }

    @Override
    public String getKeyAlias() {
        return null;
    }

    @Override
    public void setKeyLength(int keyLength) {

    }

    @Override
    public String hash(String input) {
        return null;
    }

    @Override
    public boolean validateKey(String key) {
        return false;
    }
}
