package edu.epic.cms.util;

import edu.epic.cms.model.CardRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for mapping ResultSet to CardRequest objects.
 */
@Component
public class CardRequestRowMapper implements RowMapper<CardRequest> {
    
    @Override
    public CardRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        CardRequest request = new CardRequest();
        request.setRequestId(rs.getLong("RequestId"));
        request.setCardNumber(rs.getString("CardNumber"));
        request.setRequestReasonCode(rs.getString("RequestReasonCode"));
        request.setRequestStatusCode(rs.getString("RequestStatusCode"));
        request.setRemark(rs.getString("Remark"));
        request.setCreatedTime(rs.getTimestamp("CreatedTime").toLocalDateTime());
        return request;
    }
}
