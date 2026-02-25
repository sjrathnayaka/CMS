package edu.epic.cms.repository.impl;

import edu.epic.cms.model.CardRequest;
import edu.epic.cms.repository.CardRequestRepo;
import edu.epic.cms.util.CardRequestRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(CardRequestRepoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final CardRequestRowMapper rowMapper;

    @Override
    public CardRequest save(CardRequest request) {
        String sql = "INSERT INTO CardRequest (CardNumber, RequestReasonCode, RequestStatusCode, Remark, CreatedTime, RequestedUser) "
                +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING RequestId";

        LocalDateTime now = LocalDateTime.now();

        try {
            Long requestId = jdbcTemplate.queryForObject(sql, Long.class,
                    request.getCardNumber(),
                    request.getRequestReasonCode(),
                    request.getRequestStatusCode() != null ? request.getRequestStatusCode() : "PEND",
                    request.getRemark(),
                    Timestamp.valueOf(now),
                    request.getRequestedUser() // nullable
            );

            request.setRequestId(requestId);
            request.setCreatedTime(now);

            return request;
        } catch (Exception e) {
            log.error("Error saving CardRequest: {}", request, e);
            throw e;
        }
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
    public int updateStatus(Long requestId, String statusCode, String approvedUser) {
        String sql = "UPDATE CardRequest SET RequestStatusCode = ?, ApprovedUser = ? WHERE RequestId = ?";
        return jdbcTemplate.update(sql, statusCode, approvedUser, requestId);
    }

    @Override
    public List<CardRequest> findAllFiltered(String statusCode, String typeCode, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) {
        StringBuilder sql = new StringBuilder("SELECT * FROM CardRequest WHERE 1=1");
        java.util.List<Object> args = new java.util.ArrayList<>();

        if (statusCode != null && !statusCode.isEmpty()) {
            sql.append(" AND RequestStatusCode = ?");
            args.add(statusCode);
        }
        if (typeCode != null && !typeCode.isEmpty()) {
            sql.append(" AND RequestReasonCode = ?");
            args.add(typeCode);
        }
        if (fromDate != null) {
            sql.append(" AND CreatedTime >= ?");
            args.add(Timestamp.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" AND CreatedTime <= ?");
            args.add(Timestamp.valueOf(toDate));
        }

        sql.append(" ORDER BY CreatedTime DESC");
        return jdbcTemplate.query(sql.toString(), rowMapper, args.toArray());
    }
}
