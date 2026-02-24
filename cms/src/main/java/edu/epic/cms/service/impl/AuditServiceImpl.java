package edu.epic.cms.service.impl;

import edu.epic.cms.model.AuditLog;
import edu.epic.cms.repository.AuditLogRepo;
import edu.epic.cms.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuditService to persist system activities.
 */
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepo auditLogRepo;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logActivity(String activityType, String description, String performUser) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActivityType(activityType);
        auditLog.setDescription(description);
        auditLog.setPerformUser(performUser);
        auditLogRepo.save(auditLog);
    }
}
