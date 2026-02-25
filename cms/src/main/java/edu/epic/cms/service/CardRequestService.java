package edu.epic.cms.service;

import edu.epic.cms.api.CardRequestDTO;
import edu.epic.cms.api.DetailedCardRequestResponse;
import edu.epic.cms.model.CardRequest;

import java.util.List;

/**
 * Service interface for card request operations.
 */
public interface CardRequestService {

    /**
     * Create a deactivation request for an active card.
     * Validates that creditLimit >= availableCreditLimit before allowing
     * deactivation.
     * 
     * @param request The card request details
     * @return Created card request
     * @throws edu.epic.cms.exception.CardDeactivationException if validation fails
     */
    CardRequest requestCardDeactivation(CardRequestDTO request);

    /**
     * Create an activation request for a deactivated card.
     * No validation constraints - directly activates the card.
     * 
     * @param request The card request details
     * @return Created card request
     */
    CardRequest requestCardActivation(CardRequestDTO request);

    /**
     * Get all requests for a specific card.
     */
    List<CardRequest> getRequestsByCardNumber(String encryptedCardNumber);

    /**
     * Get all card requests.
     */
    List<CardRequest> getAllRequests();

    /**
     * Get all card requests with detailed card information.
     * Includes complete card details, descriptions, and calculated balances.
     * 
     * @return List of detailed card request responses
     */
    List<DetailedCardRequestResponse> getAllRequestsWithDetails();

    List<DetailedCardRequestResponse> getAllRequestsWithDetailsFiltered(String statusCode, String typeCode,
            java.time.LocalDateTime fromDate, java.time.LocalDateTime toDate);

    /**
     * Get all requests for a specific card with detailed information.
     * 
     * @param encryptedCardNumber The encrypted card number
     * @return List of detailed card request responses for the card
     */
    List<DetailedCardRequestResponse> getRequestsByCardNumberWithDetails(String encryptedCardNumber);

    /**
     * Approve a pending card request.
     * Updates request status to APPR and performs the associated card action.
     * 
     * @param requestId    The request ID to approve
     * @param remark       Optional remark for approval
     * @param approvedUser Optional username of the approver
     * @return Updated card request
     */
    CardRequest approveRequest(Long requestId, String remark, String approvedUser);

    /**
     * Reject a pending card request.
     * Updates request status to RJCT without performing any card action.
     * 
     * @param requestId    The request ID to reject
     * @param remark       Optional remark for rejection
     * @param approvedUser Optional username of the rejecter
     * @return Updated card request
     */
    CardRequest rejectRequest(Long requestId, String remark, String approvedUser);
}
