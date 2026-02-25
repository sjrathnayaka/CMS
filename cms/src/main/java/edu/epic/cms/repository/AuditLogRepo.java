package edu.epic.cms.repository;

import edu.epic.cms.model.AuditLog;
import java.util.List;

/**
 * Repository interface for AuditLog data access.
 */
public interface AuditLogRepo {
    AuditLog save(AuditLog log);

    List<AuditLog> findAll();

    List<AuditLog> findAllFiltered(String performUser, String activityType, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate);
}
