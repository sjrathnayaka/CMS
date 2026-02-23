package edu.epic.cms.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Card API endpoints.
 * Security: Never exposes raw card numbers, only encrypted and masked versions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private String encryptedCardNumber;  // Encrypted card number (primary key)
    private String maskedCardNumber;     // Masked format: 4532XXXXXXXX9012
    private LocalDate expiryDate;
    private BigDecimal creditLimit;
    private BigDecimal cashLimit;
    private String cardStatus;
    private BigDecimal availableCreditLimit;
    private BigDecimal availableCashLimit;
    private LocalDateTime lastUpdateTime;
    private String message;
}
