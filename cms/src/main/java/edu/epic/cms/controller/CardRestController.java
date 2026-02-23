package edu.epic.cms.controller;

import edu.epic.cms.api.CardResponse;
import edu.epic.cms.api.UpdateCard;
import edu.epic.cms.api.UpdateCardRequest;
import edu.epic.cms.model.Card;
import edu.epic.cms.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardRestController {
    
    private final CardService cardService;
    
    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody UpdateCard request) {
        // Create Card from request (receives plain card number)
        Card card = new Card();
        card.setCardNumber(request.getCardNumber());  // Will be encrypted in service layer
        card.setExpiryDate(request.getExpiryDate());
        card.setCreditLimit(request.getCreditLimit());
        card.setCashLimit(request.getCashLimit());
        
        // Save card (service encrypts it)
        Card savedCard = cardService.addCard(card);
        
        // Create response with encrypted and masked card numbers only
        CardResponse response = new CardResponse(
            savedCard.getEncryptedCardNumber(),  // Encrypted version (primary key)
            savedCard.getMaskedCardNumber(),     // Masked for display
            savedCard.getExpiryDate(),
            savedCard.getCreditLimit(),
            savedCard.getCashLimit(),
            savedCard.getCardStatus(),
            savedCard.getAvailableCreditLimit(),
            savedCard.getAvailableCashLimit(),
            savedCard.getLastUpdateTime(),
            "Card created successfully"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<Card> cards = cardService.getAllCards();
        
        List<CardResponse> responses = cards.stream()
            .map(card -> new CardResponse(
                card.getEncryptedCardNumber(),  // Encrypted card number (primary key)
                card.getMaskedCardNumber(),     // Masked card number (4532XXXXXXXX9012)
                card.getExpiryDate(),
                card.getCreditLimit(),
                card.getCashLimit(),
                card.getCardStatus(),
                card.getAvailableCreditLimit(),
                card.getAvailableCashLimit(),
                card.getLastUpdateTime(),
                null
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/number/{encryptedCardNumber}")
    public ResponseEntity<CardResponse> getCardByCardNumber(@PathVariable String encryptedCardNumber) {
        Card card = cardService.getCardByCardNumber(encryptedCardNumber.trim());
        
        CardResponse response = new CardResponse(
            card.getEncryptedCardNumber(),  // Encrypted card number
            card.getMaskedCardNumber(),     // Masked card number
            card.getExpiryDate(),
            card.getCreditLimit(),
            card.getCashLimit(),
            card.getCardStatus(),
            card.getAvailableCreditLimit(),
            card.getAvailableCashLimit(),
            card.getLastUpdateTime(),
            "Card retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{encryptedCardNumber}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable String encryptedCardNumber,
            @Valid @RequestBody UpdateCardRequest request) {
        // Update card using encrypted card number (trim to handle any trailing spaces)
        Card updatedCard = cardService.updateCard(encryptedCardNumber.trim(), request);
        
        // Create response
        CardResponse response = new CardResponse(
            updatedCard.getEncryptedCardNumber(),  // Encrypted card number
            updatedCard.getMaskedCardNumber(),     // Masked card number
            updatedCard.getExpiryDate(),
            updatedCard.getCreditLimit(),
            updatedCard.getCashLimit(),
            updatedCard.getCardStatus(),
            updatedCard.getAvailableCreditLimit(),
            updatedCard.getAvailableCashLimit(),
            updatedCard.getLastUpdateTime(),
            "Card updated successfully"
        );
        
        return ResponseEntity.ok(response);
    }
}
