package edu.epic.cms.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO used when the frontend submits card creation data
 * encrypted with the server's RSA public key.
 *
 * Each field contains a Base64-encoded RSA ciphertext string
 * produced by JSEncrypt on the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedCardRequest {

    @NotBlank(message = "Encrypted card number is required")
    private String cardNumber;

    @NotBlank(message = "Encrypted expiry date is required")
    private String expiryDate;

    @NotBlank(message = "Encrypted credit limit is required")
    private String creditLimit;

    @NotBlank(message = "Encrypted cash limit is required")
    private String cashLimit;
}
