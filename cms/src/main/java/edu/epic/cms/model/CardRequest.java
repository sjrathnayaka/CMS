package edu.epic.cms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "cardrequest")
public class CardRequest {
    @Id
    private Long requestId; // Auto-generated primary key

    private String cardNumber; // Encrypted card number (foreign key to Card table)

    private String requestReasonCode; // ACTI or CDCL

    private String requestStatusCode; // PEND, APPR, RJCT

    private String remark; // Optional remark/reason

    private LocalDateTime createdTime; // Auto-set timestamp

    private String approvedUser; // FK → Users(Username), set on approval/rejection

    private String requestedUser; // FK → Users(Username), set on request creation
}
