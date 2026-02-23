package edu.epic.cms.controller;

import edu.epic.cms.api.CardResponse;
import edu.epic.cms.api.EncryptedCardRequest;
import edu.epic.cms.api.UpdateCard;
import edu.epic.cms.api.UpdateCardRequest;
import edu.epic.cms.model.Card;
import edu.epic.cms.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardRestController {

    private final CardService cardService;
    private final PrivateKey rsaPrivateKey; // injected from RsaKeyConfig

    // ─── RSA helpers ─────────────────────────────────────────────────────────

    private String rsaDecrypt(String base64Ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64Ciphertext));
        return new String(decryptedBytes, "UTF-8");
    }

    // ─── Helper: build CardResponse from Card ─────────────────────────────────

    private CardResponse toCardResponse(Card card, String message) {
        return new CardResponse(
                card.getEncryptedCardNumber(),
                card.getMaskedCardNumber(),
                card.getExpiryDate(),
                card.getCreditLimit(),
                card.getCashLimit(),
                card.getCardStatus(),
                card.getAvailableCreditLimit(),
                card.getAvailableCashLimit(),
                card.getLastUpdateTime(),
                card.getLastUpdatedUser(),
                message);
    }

    // ─── Plain card creation endpoint ─────────────────────────────────────────

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody UpdateCard request) {
        Card card = new Card();
        card.setCardNumber(request.getCardNumber());
        card.setExpiryDate(request.getExpiryDate());
        card.setCreditLimit(request.getCreditLimit());
        card.setCashLimit(request.getCashLimit());

        Card savedCard = cardService.addCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCardResponse(savedCard, "Card created successfully"));
    }

    // ─── Encrypted card creation endpoint ────────────────────────────────────

    @PostMapping("/encrypted")
    public ResponseEntity<CardResponse> createCardEncrypted(
            @Valid @RequestBody EncryptedCardRequest request) {

        try {
            log.info("Received encrypted card creation request — decrypting fields.");

            String cardNumber = rsaDecrypt(request.getCardNumber());
            String expiryStr = rsaDecrypt(request.getExpiryDate());
            String creditStr = rsaDecrypt(request.getCreditLimit());
            String cashStr = rsaDecrypt(request.getCashLimit());

            log.info("Card fields decrypted successfully.");

            LocalDate expiryDate = LocalDate.parse(expiryStr);
            BigDecimal creditLimit = new BigDecimal(creditStr);
            BigDecimal cashLimit = new BigDecimal(cashStr);

            Card card = new Card();
            card.setCardNumber(cardNumber);
            card.setExpiryDate(expiryDate);
            card.setCreditLimit(creditLimit);
            card.setCashLimit(cashLimit);

            Card savedCard = cardService.addCard(card);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toCardResponse(savedCard, "Card created successfully"));

        } catch (Exception e) {
            log.error("Failed to decrypt card creation request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ─── Get all cards ────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<Card> cards = cardService.getAllCards();
        List<CardResponse> responses = cards.stream()
                .map(card -> toCardResponse(card, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ─── Get card by encrypted card number ────────────────────────────────────

    @GetMapping("/number/{encryptedCardNumber}")
    public ResponseEntity<CardResponse> getCardByCardNumber(
            @PathVariable String encryptedCardNumber) {

        Card card = cardService.getCardByCardNumber(encryptedCardNumber.trim());
        return ResponseEntity.ok(toCardResponse(card, "Card retrieved successfully"));
    }

    // ─── Update card ──────────────────────────────────────────────────────────

    @PutMapping("/{encryptedCardNumber}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable String encryptedCardNumber,
            @Valid @RequestBody UpdateCardRequest request) {

        Card updatedCard = cardService.updateCard(encryptedCardNumber.trim(), request);
        return ResponseEntity.ok(toCardResponse(updatedCard, "Card updated successfully"));
    }
}
