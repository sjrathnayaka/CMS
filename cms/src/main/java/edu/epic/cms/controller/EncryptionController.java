package edu.epic.cms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

/**
 * Exposes the RSA public key so the frontend (JSEncrypt) can
 * encrypt card data before sending it over the wire.
 *
 * GET /api/encryption/public-key
 * Returns: { "publicKey": "<PEM string>" }
 */
@Slf4j
@RestController
@RequestMapping("/api/encryption")
@RequiredArgsConstructor
public class EncryptionController {

    private final PublicKey rsaPublicKey;

    /**
     * Returns the RSA-2048 public key in PEM format.
     * The frontend wraps this in JSEncrypt to produce encrypted payloads.
     */
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        try {
            if (rsaPublicKey == null) {
                log.error("RSA public key is null!");
                throw new IllegalStateException("RSA public key not initialized");
            }

            // Encode key bytes as Base64 and wrap in PEM headers
            String base64Key = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());

            // Build standard PEM format (X.509 / SubjectPublicKeyInfo)
            // Split into 64-character lines
            StringBuilder pemBuilder = new StringBuilder("-----BEGIN PUBLIC KEY-----\n");
            int index = 0;
            while (index < base64Key.length()) {
                int endIndex = Math.min(index + 64, base64Key.length());
                pemBuilder.append(base64Key, index, endIndex).append("\n");
                index = endIndex;
            }
            pemBuilder.append("-----END PUBLIC KEY-----");
            
            String pem = pemBuilder.toString();

            log.debug("Serving RSA public key to client.");
            return ResponseEntity.ok(Map.of("publicKey", pem));
        } catch (Exception e) {
            log.error("Error generating public key PEM: ", e);
            throw e;
        }
    }
}
