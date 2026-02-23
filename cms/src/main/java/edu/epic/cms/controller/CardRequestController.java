package edu.epic.cms.controller;

import edu.epic.cms.api.ApproveRejectRequestDTO;
import edu.epic.cms.api.CardRequestDTO;
import edu.epic.cms.api.CardRequestResponse;
import edu.epic.cms.api.DetailedCardRequestResponse;
import edu.epic.cms.model.Card;
import edu.epic.cms.model.CardRequest;
import edu.epic.cms.service.CardRequestService;
import edu.epic.cms.service.CardService;
import edu.epic.cms.service.EncryptionService;
import edu.epic.cms.util.Util;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for card request operations.
 * Handles activation and deactivation requests.
 */
@RestController
@RequestMapping("/api/card-requests")
@RequiredArgsConstructor
public class CardRequestController {
    
    private final CardRequestService cardRequestService;
    private final CardService cardService;
    private final EncryptionService encryptionService;
    
    /**
     * Create a card deactivation request.
     * Only active cards with no outstanding balance can be deactivated.
     * Request will be created with PENDING status and requires approval.
     */
    @PostMapping("/deactivate")
    public ResponseEntity<CardRequestResponse> requestDeactivation(@Valid @RequestBody CardRequestDTO requestDTO) {
        CardRequest savedRequest = cardRequestService.requestCardDeactivation(requestDTO);
        CardRequestResponse response = buildResponse(savedRequest, "Card deactivation request created successfully (pending approval)");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Create a card activation request.
     * Can activate any inactive or deactivated card without constraints.
     * Request will be created with PENDING status and requires approval.
     */
    @PostMapping("/activate")
    public ResponseEntity<CardRequestResponse> requestActivation(@Valid @RequestBody CardRequestDTO requestDTO) {
        CardRequest savedRequest = cardRequestService.requestCardActivation(requestDTO);
        CardRequestResponse response = buildResponse(savedRequest, "Card activation request created successfully (pending approval)");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all requests for a specific card.
     */
    @GetMapping("/card/{encryptedCardNumber}")
    public ResponseEntity<List<CardRequestResponse>> getRequestsByCard(@PathVariable String encryptedCardNumber) {
        List<CardRequest> requests = cardRequestService.getRequestsByCardNumber(encryptedCardNumber.trim());
        List<CardRequestResponse> responses = requests.stream()
                .map(request -> buildResponse(request, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get all card requests.
     */
    @GetMapping
    public ResponseEntity<List<CardRequestResponse>> getAllRequests() {
        List<CardRequest> requests = cardRequestService.getAllRequests();
        List<CardRequestResponse> responses = requests.stream()
                .map(request -> buildResponse(request, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get all card requests with detailed card information.
     * Includes complete card details, status descriptions, and outstanding balances.
     */
    @GetMapping("/detailed")
    public ResponseEntity<List<DetailedCardRequestResponse>> getAllRequestsWithDetails() {
        List<DetailedCardRequestResponse> responses = cardRequestService.getAllRequestsWithDetails();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get all requests for a specific card with detailed information.
     */
    @GetMapping("/detailed/card/{encryptedCardNumber}")
    public ResponseEntity<List<DetailedCardRequestResponse>> getRequestsByCardWithDetails(@PathVariable String encryptedCardNumber) {
        List<DetailedCardRequestResponse> responses = cardRequestService.getRequestsByCardNumberWithDetails(encryptedCardNumber.trim());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Approve a pending card request.
     * Updates the request status to APPROVED and performs the associated card action.
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<CardRequestResponse> approveRequest(
            @PathVariable Long requestId,
            @RequestBody(required = false) ApproveRejectRequestDTO dto) {
        
        String remark = (dto != null) ? dto.getRemark() : null;
        CardRequest updatedRequest = cardRequestService.approveRequest(requestId, remark);
        CardRequestResponse response = buildResponse(updatedRequest, "Request approved successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reject a pending card request.
     * Updates the request status to REJECTED without performing any card action.
     */
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<CardRequestResponse> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody(required = false) ApproveRejectRequestDTO dto) {
        
        String remark = (dto != null) ? dto.getRemark() : null;
        CardRequest updatedRequest = cardRequestService.rejectRequest(requestId, remark);
        CardRequestResponse response = buildResponse(updatedRequest, "Request rejected successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Build CardRequestResponse from CardRequest with masked card number.
     */
    private CardRequestResponse buildResponse(CardRequest request, String message) {
        String maskedCardNumber = null;
        try {
            // Decrypt to create masked version for display
            String decryptedCardNumber = encryptionService.decrypt(request.getCardNumber());
            maskedCardNumber = Util.maskCardNumber(decryptedCardNumber);
        } catch (Exception e) {
            // If decryption fails, use placeholder
            maskedCardNumber = "XXXXXXXXXXXX";
        }
        
        return new CardRequestResponse(
            request.getRequestId(),
            request.getCardNumber(),
            maskedCardNumber,
            request.getRequestReasonCode(),
            request.getRequestStatusCode(),
            request.getRemark(),
            request.getCreatedTime(),
            message
        );
    }
}
