package vn.civilpro.pay.security;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
public final class AesEncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_SECRET = "CivilProEnterpriseSecretKey2026SecureTaxApp";
    public static final String PREFIX = "ENC:";

    private static final SecretKeySpec SECRET_KEY;
    private static final IvParameterSpec IV_SPEC;

    static {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));
            SECRET_KEY = new SecretKeySpec(keyBytes, "AES");
            byte[] ivBytes = new byte[16];
            System.arraycopy(keyBytes, 0, ivBytes, 0, 16);
            IV_SPEC = new IvParameterSpec(ivBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AES encryption key", e);
        }
    }

    private AesEncryptionUtil() {}

    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) return plainText;
        if (plainText.startsWith(PREFIX)) return plainText;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, IV_SPEC);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return PREFIX + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Error encrypting value: {}", e.getMessage());
            return plainText;
        }
    }

    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isBlank()) return cipherText;
        if (!cipherText.startsWith(PREFIX)) return cipherText;
        try {
            String rawBase64 = cipherText.substring(PREFIX.length());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, IV_SPEC);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(rawBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting value: {}", e.getMessage());
            return cipherText;
        }
    }
}
