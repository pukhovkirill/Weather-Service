package service.encryption;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

public class KeyStorage {

    private static final String KEY_STORAGE_PATH = "src/main/resources/key_storage.jceks";

    public static void storingIntoKeyStore(String key, String alias){
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            char[] password = "d5210aef4e902bb04f7".toCharArray();
            java.io.FileInputStream fis = new FileInputStream(KEY_STORAGE_PATH);
            keyStore.load(fis, password);

            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
            SecretKey mySecretKey = new SecretKeySpec(key.getBytes(), "DSA");
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(mySecretKey);

            keyStore.setEntry(alias, secretKeyEntry, protectionParam);

            java.io.FileOutputStream fos;
            fos = new java.io.FileOutputStream(KEY_STORAGE_PATH);
            keyStore.store(fos, password);
            System.out.println("data stored");
        } catch (NoSuchAlgorithmException |
                 CertificateException |
                 KeyStoreException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey retrievingFromKeyStore(String alias){
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            char[] password = "d5210aef4e902bb04f7".toCharArray();
            java.io.FileInputStream fis = new FileInputStream(KEY_STORAGE_PATH);
            keyStore.load(fis, password);

            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
            KeyStore.Entry secretEntry =  keyStore.getEntry(alias, protectionParam);
            KeyStore.SecretKeyEntry secretKeyEntry = ((KeyStore.SecretKeyEntry) secretEntry);

            return secretKeyEntry.getSecretKey();
        } catch (UnrecoverableEntryException |
                 NoSuchAlgorithmException |
                 CertificateException |
                 KeyStoreException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }
}
