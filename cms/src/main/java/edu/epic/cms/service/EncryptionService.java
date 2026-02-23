package edu.epic.cms.service;

/**
 * Service for encrypting and decrypting sensitive data.
 * Uses AES-256 encryption with Spring Security Crypto.
 */
public interface EncryptionService {
    
    /**
     * Encrypts plain text using AES-256 encryption.
     * 
     * @param plainText the text to encrypt
     * @return the encrypted text (Base64 encoded)
     */
    String encrypt(String plainText);
    
    /**
     * Decrypts encrypted text back to plain text.
     * 
     * @param encryptedText the encrypted text (Base64 encoded)
     * @return the decrypted plain text
     */
    String decrypt(String encryptedText);
}
