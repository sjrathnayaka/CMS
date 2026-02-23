package edu.epic.cms.repository.impl;

import edu.epic.cms.model.CardRequest;
import edu.epic.cms.repository.CardRequestRepo;
import edu.epic.cms.util.CardRequestRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CardRequestRepo.
 */
@Repository
@RequiredArgsConstructor
public class CardRequestRepoImpl implements CardRequestRepo {
    
    private final JdbcTemplate jdbcTemplate;
    private final CardRequestRowMapper rowMapper;
    
    @Override
    public CardRequest save(CardRequest request) {
        String sql = "INSERT INTO CardRequest (CardNumber, RequestReasonCode, RequestStatusCode, Remark, CreatedTime) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING RequestId";
        
        LocalDateTime now = LocalDateTime.now();
        
        Long requestId = jdbcTemplate.queryForObject(sql, Long.class,
            request.getCardNumber(),
            request.getRequestReasonCode(),
            request.getRequestStatusCode() != null ? request.getRequestStatusCode() : "PEND",
            request.getRemark(),
            Timestamp.valueOf(now)
        );
        
        request.setRequestId(requestId);
        request.setCreatedTime(now);
        
        return request;
    }
    
    @Override
    public Optional<CardRequest> findById(Long requestId) {
        String sql = "SELECT * FROM CardRequest WHERE RequestId = ?";
        try {
            CardRequest request = jdbcTemplate.queryForObject(sql, rowMapper, requestId);
            return Optional.ofNullable(request);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<CardRequest> findByCardNumber(String cardNumber) {
        String sql = "SELECT * FROM CardRequest WHERE CardNumber = ? ORDER BY CreatedTime DESC";
        return jdbcTemplate.query(sql, rowMapper, cardNumber);
    }
    
    @Override
    public List<CardRequest> findAll() {
        String sql = "SELECT * FROM CardRequest ORDER BY CreatedTime DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public int updateStatus(Long requestId, String statusCode) {
        String sql = "UPDATE CardRequest SET RequestStatusCode = ? WHERE RequestId = ?";
        return jdbcTemplate.update(sql, statusCode, requestId);
    }
}
