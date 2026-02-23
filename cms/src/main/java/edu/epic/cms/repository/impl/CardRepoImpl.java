package edu.epic.cms.repository.impl;

import edu.epic.cms.util.CardRowMapper;
import edu.epic.cms.model.Card;
import edu.epic.cms.repository.CardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CardRepoImpl implements CardRepo {

    private final JdbcTemplate jdbcTemplate;
    private final CardRowMapper cardRowMapper;

    @Override
    public Card save(Card card) {
        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO Card (CardNumber, ExpiryDate, CreditLimit, CashLimit, " +
                "CardStatus, AvailableCreditLimit, AvailableCashLimit, LastUpdateTime, LastUpdatedUser) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                card.getCardNumber(),
                java.sql.Date.valueOf(card.getExpiryDate()),
                card.getCreditLimit(),
                card.getCashLimit(),
                card.getCardStatus() != null ? card.getCardStatus() : "IACT",
                card.getAvailableCreditLimit() != null ? card.getAvailableCreditLimit() : card.getCreditLimit(),
                card.getAvailableCashLimit() != null ? card.getAvailableCashLimit() : card.getCashLimit(),
                Timestamp.valueOf(now),
                card.getLastUpdatedUser() // nullable
        );

        card.setLastUpdateTime(now);

        return card;
    }

    @Override
    public Optional<Card> findById(Long id) {
        // Since CardNumber is the primary key, this method is not applicable
        // Keep for interface compatibility but should not be used
        throw new UnsupportedOperationException("Use findByCardNumber instead - CardNumber is the primary key");
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        String sql = "SELECT * FROM Card WHERE CardNumber = ?";
        try {
            Card card = jdbcTemplate.queryForObject(sql, cardRowMapper, cardNumber);
            return Optional.ofNullable(card);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        String sql = "SELECT COUNT(*) FROM Card WHERE CardNumber = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cardNumber);
        return count != null && count > 0;
    }

    @Override
    public List<Card> findAll() {
        String sql = "SELECT * FROM Card ORDER BY LastUpdateTime DESC";
        return jdbcTemplate.query(sql, cardRowMapper);
    }

    @Override
    public int update(Card card) {
        String sql = "UPDATE Card SET ExpiryDate = ?, CreditLimit = ?, " +
                "CashLimit = ?, CardStatus = ?, AvailableCreditLimit = ?, " +
                "AvailableCashLimit = ?, LastUpdateTime = ?, LastUpdatedUser = ? WHERE CardNumber = ?";

        return jdbcTemplate.update(sql,
                java.sql.Date.valueOf(card.getExpiryDate()),
                card.getCreditLimit(),
                card.getCashLimit(),
                card.getCardStatus(),
                card.getAvailableCreditLimit(),
                card.getAvailableCashLimit(),
                Timestamp.valueOf(LocalDateTime.now()),
                card.getLastUpdatedUser(), // nullable
                card.getCardNumber());
    }

    @Override
    public int deleteById(Long id) {
        // Since CardNumber is the primary key, this method is not applicable
        throw new UnsupportedOperationException("Use deleteByCardNumber instead - CardNumber is the primary key");
    }
}
