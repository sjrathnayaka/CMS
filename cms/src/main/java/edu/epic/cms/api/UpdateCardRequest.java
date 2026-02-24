package edu.epic.cms.api;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing card.
 * Only contains the editable fields (not CardNumber, CardStatus, or Available
 * Limits).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCardRequest {

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
