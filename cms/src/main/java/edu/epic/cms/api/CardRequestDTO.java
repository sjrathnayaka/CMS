package edu.epic.cms.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a card activation/deactivation request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDTO {

    @NotBlank(message = "Encrypted card number is required")
    private String encryptedCardNumber;

    private String remark; // Optional remark for the request

    private String requestedUser; // Optional: username of the requestor
}
