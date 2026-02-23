package edu.epic.cms.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility to encrypt card numbers for sample data in schema.sql
 * Run this as a standalone program to generate encrypted card numbers.
 * Uses deterministic AES encryption (same as EncryptionServiceImpl).
 */
public class CardNumberEncryptor {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    public static void main(String[] args) throws Exception {
        // Use the same secret key and salt from application.properties
        String secretKey = "MySecureCardEncryptionKey2026!";
        String salt = "deadbeefdeadbeefdeadbeefdeadbeef";
        
        // Create the key (same logic as EncryptionServiceImpl)
        String combinedKey = secretKey + salt;
        byte[] keyBytes = combinedKey.getBytes();
        byte[] key = new byte[32];
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        
        // Sample card numbers to encrypt
        String[] cardNumbers = {
            "4532015112830366",
            "5425233430109903",
            "6011111111111117",
            "378282246310005"
        };
        
        System.out.println("Encrypted Card Numbers (Deterministic AES):");
        System.out.println("============================================");
        
        for (String cardNumber : cardNumbers) {
            String encrypted = encrypt(cardNumber, secretKeySpec);
            System.out.println("Plain: " + cardNumber);
            System.out.println("Encrypted: " + encrypted);
            
            // Verify decryption works
            String decrypted = decrypt(encrypted, secretKeySpec);
            System.out.println("Decrypted: " + decrypted);
            System.out.println("Match: " + cardNumber.equals(decrypted));
            System.out.println();
        }
    }
    
    private static String encrypt(String plainText, SecretKeySpec secretKeySpec) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        // Use URL-safe Base64 encoding (no padding)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }
    
    private static String decrypt(String encryptedText, SecretKeySpec secretKeySpec) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        // Use URL-safe Base64 decoding
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}
