package edu.epic.cms.service.impl;

import edu.epic.cms.service.EncryptionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Implementation of EncryptionService using AES-256 encryption.
 * Uses deterministic encryption (same plaintext = same ciphertext) to allow searching.
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {
    
    @Value("${encryption.secret-key}")
    private String secretKey;
    
    @Value("${encryption.salt}")
    private String salt;
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    private SecretKeySpec secretKeySpec;
    
    @PostConstruct
    public void init() {
        // Create a 256-bit key from the secret key
        // Combine secret key and salt for additional entropy
        String combinedKey = secretKey + salt;
        byte[] keyBytes = combinedKey.getBytes();
        
        // Ensure we have exactly 32 bytes (256 bits) for AES-256
        byte[] key = new byte[32];
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
        
        this.secretKeySpec = new SecretKeySpec(key, ALGORITHM);
    }
    
    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            // Use URL-safe Base64 encoding (replaces + with -, / with _)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }
    
    @Override
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            // Use URL-safe Base64 decoding
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }
}

