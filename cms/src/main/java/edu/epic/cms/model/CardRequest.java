package edu.epic.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CardRequest entity representing a card activation/deactivation request.
 * Maps to CardRequest table in database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {
    
    private Long requestId;  // Auto-generated primary key
    
    private String cardNumber;  // Encrypted card number (foreign key to Card table)
    
    private String requestReasonCode;  // ACTI or CDCL
    
    private String requestStatusCode;  // PEND, APPR, RJCT
    
    private String remark;  // Optional remark/reason
    
    private LocalDateTime createdTime;  // Auto-set timestamp
}
