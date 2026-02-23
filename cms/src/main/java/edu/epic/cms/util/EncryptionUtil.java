package edu.epic.cms.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for encrypting and decrypting sensitive card data.
 * Uses AES-256 encryption algorithm.
 */
@Component
public class EncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    @Value("${card.encryption.secret-key:MySecretKey12345MySecretKey12345}")
    private String secretKeyString;
    
    /**
     * Encrypts the given plain text using AES encryption.
     * 
     * @param plainText the text to encrypt (e.g., card number)
     * @return encrypted text encoded in Base64
     * @throws Exception if encryption fails
     */
    public String encrypt(String plainText) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text cannot be null or empty");
        }
        
        SecretKey secretKey = getSecretKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypts the given encrypted text using AES decryption.
     * 
     * @param encryptedText the encrypted text in Base64 format
     * @return decrypted plain text
     * @throws Exception if decryption fails
     */
    public String decrypt(String encryptedText) throws Exception {
        if (encryptedText == null || encryptedText.isEmpty()) {
            throw new IllegalArgumentException("Encrypted text cannot be null or empty");
        }
        
        SecretKey secretKey = getSecretKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        
        return new String(decryptedBytes, "UTF-8");
    }
    
    /**
     * Gets the secret key for encryption/decryption.
     * Uses the configured secret key from application.properties.
     * 
     * @return SecretKey for AES encryption
     */
    private SecretKey getSecretKey() {
        // Ensure the key is exactly 32 bytes (256 bits) for AES-256
        String key = secretKeyString;
        if (key.length() < 32) {
            // Pad with zeros if too short
            key = String.format("%-32s", key).replace(' ', '0');
        } else if (key.length() > 32) {
            // Truncate if too long
            key = key.substring(0, 32);
        }
        
        byte[] keyBytes = key.getBytes();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    /**
     * Generates a new random AES secret key.
     * This can be used to generate a secure key for production use.
     * 
     * @return Base64 encoded secret key
     * @throws NoSuchAlgorithmException if AES algorithm is not available
     */
    public static String generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256); // AES-256
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
