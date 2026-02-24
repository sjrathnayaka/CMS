package edu.epic.cms.controller;

import edu.epic.cms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for downloading reports in PDF and CSV formats.
 * Base path: /api/reports
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ─────────────────────────── CARDS ───────────────────────────

    @GetMapping("/cards/pdf")
    public ResponseEntity<byte[]> downloadCardsPdf() throws IOException {
        byte[] data = reportService.generateCardsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cards_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/cards/csv")
    public ResponseEntity<byte[]> downloadCardsCsv() throws IOException {
        byte[] data = reportService.generateCardsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cards_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    // ─────────────────────────── CARD REQUESTS ───────────────────────────

    @GetMapping("/card-requests/pdf")
    public ResponseEntity<byte[]> downloadCardRequestsPdf() throws IOException {
        byte[] data = reportService.generateCardRequestsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"card_requests_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/card-requests/csv")
    public ResponseEntity<byte[]> downloadCardRequestsCsv() throws IOException {
        byte[] data = reportService.generateCardRequestsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"card_requests_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    // ─────────────────────────── APPROVALS ───────────────────────────

    @GetMapping("/approvals/pdf")
    public ResponseEntity<byte[]> downloadApprovalsPdf() throws IOException {
        byte[] data = reportService.generateApprovalsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"approvals_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/approvals/csv")
    public ResponseEntity<byte[]> downloadApprovalsCsv() throws IOException {
        byte[] data = reportService.generateApprovalsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"approvals_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/audit/pdf")
    public ResponseEntity<byte[]> downloadAuditPdf() throws IOException {
        byte[] data = reportService.generateAuditPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/audit/csv")
    public ResponseEntity<byte[]> downloadAuditCsv() throws IOException {
        byte[] data = reportService.generateAuditCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
