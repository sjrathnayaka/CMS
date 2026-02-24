package edu.epic.cms.repository.impl;

import edu.epic.cms.model.AuditLog;
import edu.epic.cms.repository.AuditLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JDBC implementation of AuditLog repository.
 */
@Repository
@RequiredArgsConstructor
public class AuditLogRepoImpl implements AuditLogRepo {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AuditLog> rowMapper = new RowMapper<AuditLog>() {
        @Override
        public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuditLog auditLog = new AuditLog();
            auditLog.setLogId(rs.getLong("LogId"));
            auditLog.setActivityType(rs.getString("ActivityType"));
            auditLog.setDescription(rs.getString("Description"));
            auditLog.setPerformUser(rs.getString("PerformUser"));
            auditLog.setLogTime(rs.getTimestamp("LogTime").toLocalDateTime());
            return auditLog;
        }
    };

    @Override
    public AuditLog save(AuditLog auditLog) {
        String sql = "INSERT INTO AuditLog (ActivityType, Description, PerformUser, LogTime) VALUES (?, ?, ?, ?) RETURNING LogId";

        LocalDateTime now = LocalDateTime.now();
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                auditLog.getActivityType(),
                auditLog.getDescription(),
                auditLog.getPerformUser(),
                Timestamp.valueOf(now));

        auditLog.setLogId(id);
        auditLog.setLogTime(now);
        return auditLog;
    }

    @Override
    public List<AuditLog> findAll() {
        String sql = "SELECT * FROM AuditLog ORDER BY LogTime DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
