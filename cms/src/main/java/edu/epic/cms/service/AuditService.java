package edu.epic.cms.service;

/**
 * Service interface for audit logging.
 */
public interface AuditService {
    void logActivity(String activityType, String description, String performUser);
}
