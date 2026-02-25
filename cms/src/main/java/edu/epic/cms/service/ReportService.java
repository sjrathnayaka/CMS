package edu.epic.cms.service;

import java.io.IOException;

/**
 * Service interface for generating reports in PDF and CSV formats.
 * Covers Cards, Card Requests, and Approvals (approved/rejected requests).
 */
public interface ReportService {

    byte[] generateCardsPdf(String status, java.time.LocalDate fromDate, java.time.LocalDate toDate) throws IOException;

    byte[] generateCardsCsv(String status, java.time.LocalDate fromDate, java.time.LocalDate toDate) throws IOException;

    byte[] generateCardRequestsPdf(String statusCode, String typeCode, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) throws IOException;

    byte[] generateCardRequestsCsv(String statusCode, String typeCode, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) throws IOException;

    byte[] generateApprovalsPdf(String statusCode, String typeCode, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) throws IOException;

    byte[] generateApprovalsCsv(String statusCode, String typeCode, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) throws IOException;

    byte[] generateAuditPdf(String performUser, String activityType, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) throws IOException;

    byte[] generateAuditCsv(String performUser, String activityType, java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate) throws IOException;
}
