package edu.epic.cms.api;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new card.
 * Only contains fields that should be manually input.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCard {
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,16}", message = "Card number must be 13-16 digits")
    private String cardNumber;
    
    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
    
    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit must be >= 0")
    private BigDecimal creditLimit;
    
    @NotNull(message = "Cash limit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cash limit must be >= 0")
    private BigDecimal cashLimit;
}
