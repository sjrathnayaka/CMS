package edu.epic.cms.service;

import edu.epic.cms.api.UpdateCardRequest;
import edu.epic.cms.model.Card;

import java.util.List;

public interface CardService {
    
    Card addCard(Card card);
    
    Card getCardById(Long id);
    
    /**
     * Get card by encrypted card number (primary key).
     * @param encryptedCardNumber The encrypted card number
     * @return Card with encrypted and masked card numbers
     */
    Card getCardByCardNumber(String encryptedCardNumber);
    
    /**
     * Update card using encrypted card number (primary key).
     * @param encryptedCardNumber The encrypted card number
     * @param updateRequest Update request with new values
     * @return Updated card
     */
    Card updateCard(String encryptedCardNumber, UpdateCardRequest updateRequest);
    
    List<Card> getAllCards();
}
