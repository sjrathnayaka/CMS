package edu.epic.cms.util;

import edu.epic.cms.model.Card;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CardRowMapper implements RowMapper<Card> {
    
    @Override
    public Card mapRow(ResultSet rs, int rowNum) throws SQLException {
        Card card = new Card();
        card.setCardNumber(rs.getString("CardNumber"));
        card.setExpiryDate(rs.getDate("ExpiryDate").toLocalDate());
        card.setCardStatus(rs.getString("CardStatus"));
        card.setCreditLimit(rs.getBigDecimal("CreditLimit"));
        card.setCashLimit(rs.getBigDecimal("CashLimit"));
        card.setAvailableCreditLimit(rs.getBigDecimal("AvailableCreditLimit"));
        card.setAvailableCashLimit(rs.getBigDecimal("AvailableCashLimit"));
        card.setLastUpdateTime(rs.getTimestamp("LastUpdateTime").toLocalDateTime());
        return card;
    }
}
