package edu.epic.cms.repository;

import edu.epic.cms.model.CardRequest;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CardRequest entity.
 */
public interface CardRequestRepo {

    /**
     * Save a new card request.
     */
    CardRequest save(CardRequest request);

    /**
     * Find a card request by ID.
     */
    Optional<CardRequest> findById(Long requestId);

    /**
     * Find all card requests for a specific card.
     */
    List<CardRequest> findByCardNumber(String cardNumber);

    /**
     * Find all card requests.
     */
    List<CardRequest> findAll();

    /**
     * Update request status and optionally set the approving/rejecting user.
     */
    int updateStatus(Long requestId, String statusCode, String approvedUser);

    List<CardRequest> findAllFiltered(String statusCode, String typeCode, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate);
}
