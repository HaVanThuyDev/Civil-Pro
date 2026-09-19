package vn.civilpro.pay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.civilpro.pay.security.Aes256EncryptConverter;
import vn.civilpro.pay.security.AesEncryptionUtil;

import static org.junit.jupiter.api.Assertions.*;

class AesEncryptionUtilTest {

    private final Aes256EncryptConverter converter = new Aes256EncryptConverter();

    @Test
    @DisplayName("AES-256 Encryption & Decryption Round Trip")
    void testEncryptAndDecrypt_RoundTrip() {
        String cccd = "001200000001";
        String bankAccount = "19038291028301";

        String encCccd = AesEncryptionUtil.encrypt(cccd);
        String encBank = AesEncryptionUtil.encrypt(bankAccount);

        assertNotNull(encCccd);
        assertTrue(encCccd.startsWith("ENC:"));
        assertNotEquals(cccd, encCccd);

        assertNotNull(encBank);
        assertTrue(encBank.startsWith("ENC:"));

        assertEquals(cccd, AesEncryptionUtil.decrypt(encCccd));
        assertEquals(bankAccount, AesEncryptionUtil.decrypt(encBank));
    }

    @Test
    @DisplayName("Deterministic Encryption for Exact-Match DB Search")
    void testEncrypt_Deterministic() {
        String original = "001200000001";
        String enc1 = AesEncryptionUtil.encrypt(original);
        String enc2 = AesEncryptionUtil.encrypt(original);

        assertEquals(enc1, enc2, "Deterministic encryption must produce identical ciphertext for database indexes");
    }

    @Test
    @DisplayName("Fallback for Legacy Unencrypted Plain Text")
    void testDecrypt_PlainTextFallback() {
        String legacyPlainText = "001200000099";
        String decrypted = AesEncryptionUtil.decrypt(legacyPlainText);

        assertEquals(legacyPlainText, decrypted, "Plaintext without ENC: prefix must remain intact");
    }

    @Test
    @DisplayName("JPA AttributeConverter converts correctly")
    void testConverter() {
        String account = "9704198273619283";
        String dbColumn = converter.convertToDatabaseColumn(account);
        assertTrue(dbColumn.startsWith("ENC:"));

        String entityAttr = converter.convertToEntityAttribute(dbColumn);
        assertEquals(account, entityAttr);
    }
}
