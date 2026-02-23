package edu.epic.cms.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for card request operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestResponse {
    
    private Long requestId;
    private String encryptedCardNumber;
    private String maskedCardNumber;
    private String requestReasonCode;
    private String requestStatusCode;
    private String remark;
    private LocalDateTime createdTime;
    private String message;
}
