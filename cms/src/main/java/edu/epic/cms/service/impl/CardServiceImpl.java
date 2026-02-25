package edu.epic.cms.service.impl;

import edu.epic.cms.api.UpdateCardRequest;
import edu.epic.cms.enums.CardStatus;
import edu.epic.cms.exception.CardNotFoundException;
import edu.epic.cms.exception.DuplicateCardException;
import edu.epic.cms.exception.InvalidCardException;
import edu.epic.cms.model.Card;
import edu.epic.cms.repository.CardRepo;
import edu.epic.cms.service.CardService;
import edu.epic.cms.service.EncryptionService;
import edu.epic.cms.service.AuditService;
import edu.epic.cms.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepo cardRepo;
    private final EncryptionService encryptionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public Card addCard(Card card) {
        try {
            // Store the original card number for encryption
            String originalCardNumber = card.getCardNumber();

            // Encrypt the card number before saving
            String encryptedCardNumber = encryptionService.encrypt(originalCardNumber);
            log.info("Card number encrypted successfully");

            // Check if encrypted card number already exists
            if (cardRepo.existsByCardNumber(encryptedCardNumber)) {
                throw new DuplicateCardException("Card already exists");
            }

            // Set the encrypted card number (this is what gets stored in DB)
            card.setCardNumber(encryptedCardNumber);

            // Set default status to IACT (Inactive)
            card.setCardStatus(CardStatus.IACT.getCode());

            // Set available limits equal to total limits initially
            if (card.getAvailableCreditLimit() == null) {
                card.setAvailableCreditLimit(card.getCreditLimit());
            }
            if (card.getAvailableCashLimit() == null) {
                card.setAvailableCashLimit(card.getCashLimit());
            }

            Card savedCard = cardRepo.save(card);

            auditService.logActivity("CARD_ADDED",
                    "New card added with masked number: " + Util.maskCardNumber(originalCardNumber),
                    card.getLastUpdatedUser());

            // Store encrypted card number for response
            savedCard.setEncryptedCardNumber(encryptedCardNumber);
            // Create masked version for display
            savedCard.setMaskedCardNumber(Util.maskCardNumber(originalCardNumber));

            return savedCard;

        } catch (DuplicateCardException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error encrypting card number: {}", e.getMessage());
            throw new InvalidCardException("Failed to encrypt card number: " + e.getMessage());
        }
    }

    @Override
    public Card getCardById(Long id) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        // Decrypt the card number before returning
        try {
            card.setCardNumber(encryptionService.decrypt(card.getCardNumber()));
        } catch (Exception e) {
            log.error("Error decrypting card number for card ID {}: {}", id, e.getMessage());
            throw new InvalidCardException("Failed to decrypt card number: " + e.getMessage());
        }

        return card;
    }

    @Override
    public Card getCardByCardNumber(String encryptedCardNumber) {
        try {
            // Search using encrypted card number directly
            Card card = cardRepo.findByCardNumber(encryptedCardNumber)
                    .orElseThrow(() -> new CardNotFoundException("card number", encryptedCardNumber));

            // Decrypt for masked display only
            String decryptedCardNumber = encryptionService.decrypt(encryptedCardNumber);

            // Store encrypted card number (primary key)
            card.setEncryptedCardNumber(encryptedCardNumber);
            // Create masked version for display
            card.setMaskedCardNumber(Util.maskCardNumber(decryptedCardNumber));

            return card;
        } catch (CardNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving card: {}", e.getMessage());
            throw new InvalidCardException("Failed to retrieve card: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Card updateCard(String encryptedCardNumber, UpdateCardRequest updateRequest) {
        // Fetch existing card by encrypted card number (primary key)
        Card existingCard = getCardByCardNumber(encryptedCardNumber);

        // Calculate the difference in limits
        BigDecimal creditLimitDiff = updateRequest.getCreditLimit().subtract(existingCard.getCreditLimit());
        BigDecimal cashLimitDiff = updateRequest.getCashLimit().subtract(existingCard.getCashLimit());

        // Update only the editable fields
        existingCard.setExpiryDate(updateRequest.getExpiryDate());
        existingCard.setCreditLimit(updateRequest.getCreditLimit());
        existingCard.setCashLimit(updateRequest.getCashLimit());

        // Adjust available limits based on the difference
        BigDecimal newAvailableCreditLimit = existingCard.getAvailableCreditLimit().add(creditLimitDiff);
        BigDecimal newAvailableCashLimit = existingCard.getAvailableCashLimit().add(cashLimitDiff);

        // Ensure available limits don't exceed total limits and aren't negative
        existingCard.setAvailableCreditLimit(
                newAvailableCreditLimit.min(updateRequest.getCreditLimit()).max(BigDecimal.ZERO));
        existingCard.setAvailableCashLimit(
                newAvailableCashLimit.min(updateRequest.getCashLimit()).max(BigDecimal.ZERO));

        // Set the encrypted card number for update (primary key)
        existingCard.setCardNumber(encryptedCardNumber);

        // Update the card in database
        cardRepo.update(existingCard);

        auditService.logActivity("CARD_UPDATED",
                "Card updated: " + encryptedCardNumber,
                existingCard.getLastUpdatedUser());

        // Return the updated card
        return getCardByCardNumber(encryptedCardNumber);
    }

    @Override
    public List<Card> getAllCards() {
        return processCards(cardRepo.findAll());
    }

    @Override
    public List<Card> getAllCardsFiltered(String status, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        return processCards(cardRepo.findAllFiltered(status, fromDate, toDate));
    }

    private List<Card> processCards(List<Card> cards) {
        return cards.stream()
                .filter(card -> {
                    try {
                        String encryptedCardNumber = card.getCardNumber();
                        card.setEncryptedCardNumber(encryptedCardNumber);
                        String decryptedCardNumber = encryptionService.decrypt(encryptedCardNumber);
                        card.setMaskedCardNumber(Util.maskCardNumber(decryptedCardNumber));
                        return true;
                    } catch (Exception e) {
                        log.warn("Skipping card that cannot be decrypted: {}", e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}
