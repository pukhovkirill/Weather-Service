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
import java.util.UUID;

public class KeyStorage {

    public static String storingIntoKeyStore(String key){
        try {
            String alias = UUID.randomUUID().toString();
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            char[] password = "changeit".toCharArray();
            String path = "$JAVA_SECURITY/cacerts";
            java.io.FileInputStream fis = new FileInputStream(path);
            keyStore.load(fis, password);

            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
            SecretKey mySecretKey = new SecretKeySpec(key.getBytes(), "DSA");
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(mySecretKey);

            keyStore.setEntry(alias, secretKeyEntry, protectionParam);

            return alias;
        } catch (NoSuchAlgorithmException |
                 CertificateException |
                 KeyStoreException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String retrievingFromKeyStore(String alias){
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            char[] password = "changeit".toCharArray();
            String path = "$JAVA_SECURITY/cacerts";
            java.io.FileInputStream fis = new FileInputStream(path);
            keyStore.load(fis, password);

            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, protectionParam);

            SecretKey mySecretKey = secretKeyEntry.getSecretKey();

            return mySecretKey.toString();
        } catch (UnrecoverableEntryException |
                 NoSuchAlgorithmException |
                 CertificateException |
                 KeyStoreException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }
}
