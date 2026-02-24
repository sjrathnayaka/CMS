package edu.epic.cms.service;

import java.io.IOException;

/**
 * Service interface for generating reports in PDF and CSV formats.
 * Covers Cards, Card Requests, and Approvals (approved/rejected requests).
 */
public interface ReportService {

    byte[] generateCardsPdf() throws IOException;

    byte[] generateCardsCsv() throws IOException;

    byte[] generateCardRequestsPdf() throws IOException;

    byte[] generateCardRequestsCsv() throws IOException;

    byte[] generateApprovalsPdf() throws IOException;

    byte[] generateApprovalsCsv() throws IOException;

    byte[] generateAuditPdf() throws IOException;

    byte[] generateAuditCsv() throws IOException;
}
