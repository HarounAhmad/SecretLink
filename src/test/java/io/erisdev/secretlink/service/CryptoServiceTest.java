package io.erisdev.secretlink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {
    private CryptoService cryptoService;

    @BeforeEach
    void setup() {
        // 32-byte key for AES-256 (must be exactly 32 bytes)
        byte[] keyBytes = "01234567890123456789012345678901".getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        cryptoService = new CryptoService(keySpec);
    }

    @Test
    void encryptAndDecrypt_ShouldReturnOriginalPlaintext() {
        String plaintext = "This is a secret message";

        CryptoResult result = cryptoService.encrypt(plaintext);
        assertNotNull(result);
        assertNotNull(result.getEncryptedPayload());
        assertNotNull(result.getIv());

        String decrypted = cryptoService.decrypt(result.getEncryptedPayload(), result.getIv());
        assertEquals(plaintext, decrypted);
    }

    @Test
    void decrypt_ShouldThrow_WhenInvalidCipherText() {
        assertThrows(RuntimeException.class, () -> {
            cryptoService.decrypt("invalidBase64", "invalidIv");
        });
    }

    @Test
    void encrypt_ShouldGenerateDifferentIvEachTime() {
        String plaintext = "repeat test";
        CryptoResult first = cryptoService.encrypt(plaintext);
        CryptoResult second = cryptoService.encrypt(plaintext);

        assertNotEquals(first.getIv(), second.getIv());

        assertNotEquals(first.getEncryptedPayload(), second.getEncryptedPayload());
    }
}