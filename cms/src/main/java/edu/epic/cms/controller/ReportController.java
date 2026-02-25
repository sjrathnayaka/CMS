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
    public ResponseEntity<byte[]> downloadCardsPdf(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String status,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate toDate)
            throws IOException {
        byte[] data = reportService.generateCardsPdf(status, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cards_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/cards/csv")
    public ResponseEntity<byte[]> downloadCardsCsv(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String status,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate toDate)
            throws IOException {
        byte[] data = reportService.generateCardsCsv(status, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cards_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    // ─────────────────────────── CARD REQUESTS ───────────────────────────

    @GetMapping("/card-requests/pdf")
    public ResponseEntity<byte[]> downloadCardRequestsPdf(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String statusCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String typeCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate)
            throws IOException {
        byte[] data = reportService.generateCardRequestsPdf(statusCode, typeCode, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"card_requests_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/card-requests/csv")
    public ResponseEntity<byte[]> downloadCardRequestsCsv(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String statusCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String typeCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate)
            throws IOException {
        byte[] data = reportService.generateCardRequestsCsv(statusCode, typeCode, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"card_requests_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    // ─────────────────────────── APPROVALS ───────────────────────────

    @GetMapping("/approvals/pdf")
    public ResponseEntity<byte[]> downloadApprovalsPdf(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String statusCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String typeCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate)
            throws IOException {
        byte[] data = reportService.generateApprovalsPdf(statusCode, typeCode, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"approvals_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/approvals/csv")
    public ResponseEntity<byte[]> downloadApprovalsCsv(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String statusCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String typeCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate)
            throws IOException {
        byte[] data = reportService.generateApprovalsCsv(statusCode, typeCode, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"approvals_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    // ─────────────────────────── AUDIT ───────────────────────────

    @GetMapping("/audit/pdf")
    public ResponseEntity<byte[]> downloadAuditPdf(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String performUser,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String activityType,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate)
            throws IOException {
        byte[] data = reportService.generateAuditPdf(performUser, activityType, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/audit/csv")
    public ResponseEntity<byte[]> downloadAuditCsv(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String performUser,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String activityType,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate)
            throws IOException {
        byte[] data = reportService.generateAuditCsv(performUser, activityType, fromDate, toDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
