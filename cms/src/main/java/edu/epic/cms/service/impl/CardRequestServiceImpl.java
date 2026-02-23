package edu.epic.cms.service.impl;

import edu.epic.cms.api.CardRequestDTO;
import edu.epic.cms.api.DetailedCardRequestResponse;
import edu.epic.cms.enums.CardRequestType;
import edu.epic.cms.enums.CardStatus;
import edu.epic.cms.enums.RequestStatus;
import edu.epic.cms.exception.CardActivationException;
import edu.epic.cms.exception.CardDeactivationException;
import edu.epic.cms.exception.CardNotFoundException;
import edu.epic.cms.exception.InvalidRequestStatusException;
import edu.epic.cms.exception.RequestNotFoundException;
import edu.epic.cms.model.Card;
import edu.epic.cms.model.CardRequest;
import edu.epic.cms.repository.CardRepo;
import edu.epic.cms.repository.CardRequestRepo;
import edu.epic.cms.service.CardRequestService;
import edu.epic.cms.service.EncryptionService;
import edu.epic.cms.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CardRequestService with activation/deactivation business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardRequestServiceImpl implements CardRequestService {
    
    private final CardRequestRepo cardRequestRepo;
    private final CardRepo cardRepo;
    private final EncryptionService encryptionService;
    
    @Override
    @Transactional
    public CardRequest requestCardDeactivation(CardRequestDTO requestDTO) {
        String encryptedCardNumber = requestDTO.getEncryptedCardNumber().trim();
        
        // Fetch the card
        Card card = cardRepo.findByCardNumber(encryptedCardNumber)
                .orElseThrow(() -> new CardNotFoundException("card number", encryptedCardNumber));
        
        // Validate card is currently active
        if (!CardStatus.CACT.getCode().equals(card.getCardStatus())) {
            throw new CardDeactivationException("Card is not active. Only active cards can be deactivated.");
        }
        
        // CRITICAL VALIDATION: Check if credit limit >= available credit limit
        // This ensures the card has no outstanding balance
        if (card.getCreditLimit().compareTo(card.getAvailableCreditLimit()) > 0) {
            BigDecimal outstandingBalance = card.getCreditLimit().subtract(card.getAvailableCreditLimit());
            throw new CardDeactivationException(
                "Cannot deactivate card. You have an outstanding balance of " + 
                outstandingBalance + " to pay before deactivation."
            );
        }
        
        // Create and save the request with PENDING status
        CardRequest request = new CardRequest();
        request.setCardNumber(encryptedCardNumber);
        request.setRequestReasonCode(CardRequestType.CDCL.getCode());
        request.setRequestStatusCode(RequestStatus.PEND.getCode());  // Pending - requires approval
        request.setRemark(requestDTO.getRemark());
        
        CardRequest savedRequest = cardRequestRepo.save(request);
        
        log.info("Card deactivation request created (pending approval) for card: {}", encryptedCardNumber);
        
        return savedRequest;
    }
    
    @Override
    @Transactional
    public CardRequest requestCardActivation(CardRequestDTO requestDTO) {
        String encryptedCardNumber = requestDTO.getEncryptedCardNumber().trim();
        
        // Fetch the card
        Card card = cardRepo.findByCardNumber(encryptedCardNumber)
                .orElseThrow(() -> new CardNotFoundException("card number", encryptedCardNumber));
        
        // Validate card is currently deactivated or inactive
        if (CardStatus.CACT.getCode().equals(card.getCardStatus())) {
            throw new CardActivationException("Card is already active.");
        }
        
        // Create and save the request with PENDING status
        CardRequest request = new CardRequest();
        request.setCardNumber(encryptedCardNumber);
        request.setRequestReasonCode(CardRequestType.ACTI.getCode());
        request.setRequestStatusCode(RequestStatus.PEND.getCode());  // Pending - requires approval
        request.setRemark(requestDTO.getRemark());
        
        CardRequest savedRequest = cardRequestRepo.save(request);
        
        log.info("Card activation request created (pending approval) for card: {}", encryptedCardNumber);
        
        return savedRequest;
    }
    
    @Override
    public List<CardRequest> getRequestsByCardNumber(String encryptedCardNumber) {
        return cardRequestRepo.findByCardNumber(encryptedCardNumber.trim());
    }
    
    @Override
    public List<CardRequest> getAllRequests() {
        return cardRequestRepo.findAll();
    }
    
    @Override
    public List<DetailedCardRequestResponse> getAllRequestsWithDetails() {
        List<CardRequest> requests = cardRequestRepo.findAll();
        return requests.stream()
                .map(this::buildDetailedResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DetailedCardRequestResponse> getRequestsByCardNumberWithDetails(String encryptedCardNumber) {
        List<CardRequest> requests = cardRequestRepo.findByCardNumber(encryptedCardNumber.trim());
        return requests.stream()
                .map(this::buildDetailedResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Builds a detailed response with complete card and request information.
     */
    private DetailedCardRequestResponse buildDetailedResponse(CardRequest request) {
        // Fetch the card details
        Card card = cardRepo.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("card number", request.getCardNumber()));
        
        // Decrypt and mask card number for display
        String maskedCardNumber;
        try {
            String decryptedCardNumber = encryptionService.decrypt(request.getCardNumber());
            maskedCardNumber = Util.maskCardNumber(decryptedCardNumber);
        } catch (Exception e) {
            maskedCardNumber = "XXXXXXXXXXXX";
        }
        
        // Calculate outstanding balances
        BigDecimal outstandingCreditBalance = card.getCreditLimit().subtract(card.getAvailableCreditLimit());
        BigDecimal outstandingCashBalance = card.getCashLimit().subtract(card.getAvailableCashLimit());
        
        // Get descriptions from enums
        String requestReasonDescription = getRequestTypeDescription(request.getRequestReasonCode());
        String requestStatusDescription = getRequestStatusDescription(request.getRequestStatusCode());
        String cardStatusDescription = getCardStatusDescription(card.getCardStatus());
        
        return new DetailedCardRequestResponse(
            // Request details
            request.getRequestId(),
            request.getRequestReasonCode(),
            requestReasonDescription,
            request.getRequestStatusCode(),
            requestStatusDescription,
            request.getRemark(),
            request.getCreatedTime(),
            
            // Card details
            request.getCardNumber(),           // Encrypted card number
            maskedCardNumber,                  // Masked card number
            card.getExpiryDate(),
            card.getCreditLimit(),
            card.getCashLimit(),
            card.getCardStatus(),
            cardStatusDescription,
            card.getAvailableCreditLimit(),
            card.getAvailableCashLimit(),
            outstandingCreditBalance,
            outstandingCashBalance,
            card.getLastUpdateTime()
        );
    }
    
    /**
     * Get request type description from code.
     */
    private String getRequestTypeDescription(String code) {
        try {
            return CardRequestType.valueOf(code).getDescription();
        } catch (IllegalArgumentException e) {
            return "Unknown Request Type";
        }
    }
    
    /**
     * Get request status description from code.
     */
    private String getRequestStatusDescription(String code) {
        try {
            return RequestStatus.valueOf(code).getDescription();
        } catch (IllegalArgumentException e) {
            return "Unknown Status";
        }
    }
    
    /**
     * Get card status description from code.
     */
    private String getCardStatusDescription(String code) {
        try {
            return CardStatus.valueOf(code).getDescription();
        } catch (IllegalArgumentException e) {
            return "Unknown Card Status";
        }
    }
    
    @Override
    @Transactional
    public CardRequest approveRequest(Long requestId, String remark) {
        // Fetch the request
        CardRequest request = cardRequestRepo.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        
        // Validate request is in PENDING status
        if (!RequestStatus.PEND.getCode().equals(request.getRequestStatusCode())) {
            throw new InvalidRequestStatusException(
                "Only pending requests can be approved. Current status: " + request.getRequestStatusCode()
            );
        }
        
        // Fetch the card
        Card card = cardRepo.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("card number", request.getCardNumber()));
        
        // Perform action based on request type
        if (CardRequestType.ACTI.getCode().equals(request.getRequestReasonCode())) {
            // Activation request - validate card is not already active
            if (CardStatus.CACT.getCode().equals(card.getCardStatus())) {
                throw new CardActivationException("Card is already active.");
            }
            
            // Activate the card
            card.setCardStatus(CardStatus.CACT.getCode());
            cardRepo.update(card);
            
            log.info("Card activation request approved for card: {}", request.getCardNumber());
            
        } else if (CardRequestType.CDCL.getCode().equals(request.getRequestReasonCode())) {
            // Deactivation request - validate card is active
            if (!CardStatus.CACT.getCode().equals(card.getCardStatus())) {
                throw new CardDeactivationException("Card is not active. Only active cards can be deactivated.");
            }
            
            // Validate no outstanding balance
            if (card.getCreditLimit().compareTo(card.getAvailableCreditLimit()) > 0) {
                BigDecimal outstandingBalance = card.getCreditLimit().subtract(card.getAvailableCreditLimit());
                throw new CardDeactivationException(
                    "Cannot deactivate card. Outstanding balance of " + outstandingBalance + " must be paid first."
                );
            }
            
            // Deactivate the card
            card.setCardStatus(CardStatus.DACT.getCode());
            cardRepo.update(card);
            
            log.info("Card deactivation request approved for card: {}", request.getCardNumber());
        }
        
        // Update request status to APPROVED
        request.setRequestStatusCode(RequestStatus.APPR.getCode());
        if (remark != null && !remark.trim().isEmpty()) {
            request.setRemark(remark);
        }
        cardRequestRepo.updateStatus(requestId, RequestStatus.APPR.getCode());
        
        return request;
    }
    
    @Override
    @Transactional
    public CardRequest rejectRequest(Long requestId, String remark) {
        // Fetch the request
        CardRequest request = cardRequestRepo.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        
        // Validate request is in PENDING status
        if (!RequestStatus.PEND.getCode().equals(request.getRequestStatusCode())) {
            throw new InvalidRequestStatusException(
                "Only pending requests can be rejected. Current status: " + request.getRequestStatusCode()
            );
        }
        
        // Update request status to REJECTED
        request.setRequestStatusCode(RequestStatus.RJCT.getCode());
        if (remark != null && !remark.trim().isEmpty()) {
            request.setRemark(remark);
        }
        cardRequestRepo.updateStatus(requestId, RequestStatus.RJCT.getCode());
        
        log.info("Card request rejected: {}", requestId);
        
        return request;
    }
}
