package edu.epic.cms.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Card entity representing a credit card in the system.
 * CardNumber is the primary key as per the database schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,16}", message = "Card number must be 13-16 digits")
    private String cardNumber; // Primary Key (decrypted for business logic)

    // Transient fields for API response (not stored in DB)
    private String encryptedCardNumber; // Encrypted value from database
    private String maskedCardNumber; // Masked format: 4532XXXXXXXX9012

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    private String cardStatus; // Default: "IACT" (Inactive)

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit must be >= 0")
    private BigDecimal creditLimit;

    @NotNull(message = "Cash limit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cash limit must be >= 0")
    private BigDecimal cashLimit;

    private BigDecimal availableCreditLimit;

    private BigDecimal availableCashLimit;

    private LocalDateTime lastUpdateTime;

    private String lastUpdatedUser; // FK → Users(Username), nullable
}
