package edu.epic.cms.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detailed response DTO for card requests with complete card information.
 * Provides comprehensive view of both request and associated card details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedCardRequestResponse {
    
    // Request Details
    private Long requestId;
    private String requestReasonCode;
    private String requestReasonDescription;
    private String requestStatusCode;
    private String requestStatusDescription;
    private String remark;
    private LocalDateTime createdTime;
    
    // Card Details
    private String encryptedCardNumber;
    private String maskedCardNumber;
    private LocalDate expiryDate;
    private BigDecimal creditLimit;
    private BigDecimal cashLimit;
    private String cardStatus;
    private String cardStatusDescription;
    private BigDecimal availableCreditLimit;
    private BigDecimal availableCashLimit;
    private BigDecimal outstandingCreditBalance;
    private BigDecimal outstandingCashBalance;
    private LocalDateTime lastUpdateTime;
}
