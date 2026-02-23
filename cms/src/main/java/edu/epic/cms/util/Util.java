package edu.epic.cms.util;

/**
 * Utility class for common helper methods.
 */
public class Util {
    
    /**
     * Masks a card number by showing only the first 4 and last 4 digits.
     * Middle digits (positions 5-12 for 16-digit cards) are replaced with 'X'.
     * 
     * Examples:
     * - "4532123456789012" -> "4532XXXXXXXX9012"
     * - "5412345678901" (13 digits) -> "5412XXXXX8901"
     * 
     * @param cardNumber the card number to mask (13-16 digits)
     * @return masked card number showing first 4 and last 4 digits
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }
        
        // Get first 4 digits
        String first4 = cardNumber.substring(0, 4);
        
        // Get last 4 digits
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        
        // Calculate middle section length
        int middleLength = cardNumber.length() - 8;
        
        // Create masked middle section
        String maskedMiddle = "X".repeat(middleLength);
        
        return first4 + maskedMiddle + last4;
    }
}
