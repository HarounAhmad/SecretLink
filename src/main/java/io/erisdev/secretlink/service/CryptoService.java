package io.erisdev.secretlink.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class CryptoService {
    private final SecretKeySpec secretKeySpec;

    public CryptoService(@Value("${crypto.secret-key}") String secretKey) {
        // Key must be 32 bytes for AES-256
        byte[] keyBytes = Arrays.copyOf(secretKey.getBytes(StandardCharsets.UTF_8), 32);
        this.secretKeySpec = new SecretKeySpec(keyBytes, "AES");
    }

    public CryptoResult encrypt(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] ivBytes = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);

            return new CryptoResult(encryptedBase64, ivBase64);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedBase64, String ivBase64) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] decodedEncrypted = Base64.getDecoder().decode(encryptedBase64);

            byte[] decrypted = cipher.doFinal(decodedEncrypted);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

}
