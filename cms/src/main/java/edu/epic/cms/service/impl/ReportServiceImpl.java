package edu.epic.cms.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import edu.epic.cms.api.DetailedCardRequestResponse;
import edu.epic.cms.model.AuditLog;
import edu.epic.cms.model.Card;
import edu.epic.cms.repository.AuditLogRepo;
import edu.epic.cms.service.CardRequestService;
import edu.epic.cms.service.CardService;
import edu.epic.cms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of ReportService.
 * Generates PDF reports using iText 7 and CSV reports using OpenCSV.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CardService cardService;
    private final CardRequestService cardRequestService;
    private final AuditLogRepo auditLogRepo;

    // ─────────────────────────── CARDS ───────────────────────────

    @Override
    public byte[] generateCardsPdf() throws IOException {
        List<Card> cards = cardService.getAllCards();

        String[] headers = {
                "Masked Card No.", "Expiry Date", "Status",
                "Credit Limit", "Cash Limit",
                "Avail. Credit", "Avail. Cash",
                "Last Updated", "Last Updated By"
        };

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(bos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A3.rotate());

            addTitle(doc, "Cards Report");

            Table table = new Table(UnitValue.createPercentArray(headers.length))
                    .useAllAvailableWidth();
            addHeaderRow(table, headers);

            for (Card c : cards) {
                table.addCell(cell(c.getMaskedCardNumber()));
                table.addCell(cell(c.getExpiryDate() != null ? c.getExpiryDate().format(DATE_FMT) : ""));
                table.addCell(cell(c.getCardStatus()));
                table.addCell(cell(str(c.getCreditLimit())));
                table.addCell(cell(str(c.getCashLimit())));
                table.addCell(cell(str(c.getAvailableCreditLimit())));
                table.addCell(cell(str(c.getAvailableCashLimit())));
                table.addCell(cell(c.getLastUpdateTime() != null ? c.getLastUpdateTime().format(DT_FMT) : ""));
                table.addCell(cell(c.getLastUpdatedUser()));
            }

            doc.add(table);
            doc.close();
            return bos.toByteArray();
        }
    }

    @Override
    public byte[] generateCardsCsv() throws IOException {
        List<Card> cards = cardService.getAllCards();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                CSVWriter csv = new CSVWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {

            csv.writeNext(new String[] {
                    "Masked Card No.", "Expiry Date", "Status",
                    "Credit Limit", "Cash Limit",
                    "Avail. Credit", "Avail. Cash",
                    "Last Updated", "Last Updated By"
            });

            for (Card c : cards) {
                csv.writeNext(new String[] {
                        c.getMaskedCardNumber(),
                        c.getExpiryDate() != null ? c.getExpiryDate().format(DATE_FMT) : "",
                        c.getCardStatus(),
                        str(c.getCreditLimit()),
                        str(c.getCashLimit()),
                        str(c.getAvailableCreditLimit()),
                        str(c.getAvailableCashLimit()),
                        c.getLastUpdateTime() != null ? c.getLastUpdateTime().format(DT_FMT) : "",
                        c.getLastUpdatedUser()
                });
            }
            csv.flush();
            return bos.toByteArray();
        }
    }

    // ─────────────────────────── CARD REQUESTS ───────────────────────────

    @Override
    public byte[] generateCardRequestsPdf() throws IOException {
        List<DetailedCardRequestResponse> requests = cardRequestService.getAllRequestsWithDetails();

        String[] headers = {
                "Request ID", "Masked Card No.", "Reason", "Status",
                "Remark", "Requested By", "Created Time"
        };

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(bos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A3.rotate());

            addTitle(doc, "Card Requests Report");

            Table table = new Table(UnitValue.createPercentArray(headers.length))
                    .useAllAvailableWidth();
            addHeaderRow(table, headers);

            for (DetailedCardRequestResponse r : requests) {
                table.addCell(cell(str(r.getRequestId())));
                table.addCell(cell(r.getMaskedCardNumber()));
                table.addCell(cell(r.getRequestReasonDescription()));
                table.addCell(cell(r.getRequestStatusDescription()));
                table.addCell(cell(r.getRemark()));
                table.addCell(cell(r.getRequestedUser()));
                table.addCell(cell(r.getCreatedTime() != null ? r.getCreatedTime().format(DT_FMT) : ""));
            }

            doc.add(table);
            doc.close();
            return bos.toByteArray();
        }
    }

    @Override
    public byte[] generateCardRequestsCsv() throws IOException {
        List<DetailedCardRequestResponse> requests = cardRequestService.getAllRequestsWithDetails();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                CSVWriter csv = new CSVWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {

            csv.writeNext(new String[] {
                    "Request ID", "Masked Card No.", "Reason", "Status",
                    "Remark", "Requested By", "Created Time"
            });

            for (DetailedCardRequestResponse r : requests) {
                csv.writeNext(new String[] {
                        str(r.getRequestId()),
                        r.getMaskedCardNumber(),
                        r.getRequestReasonDescription(),
                        r.getRequestStatusDescription(),
                        r.getRemark(),
                        r.getRequestedUser(),
                        r.getCreatedTime() != null ? r.getCreatedTime().format(DT_FMT) : ""
                });
            }
            csv.flush();
            return bos.toByteArray();
        }
    }

    // ─────────────────────────── APPROVALS ───────────────────────────

    @Override
    public byte[] generateApprovalsPdf() throws IOException {
        List<DetailedCardRequestResponse> approvals = getApprovals();

        String[] headers = {
                "Request ID", "Masked Card No.", "Reason", "Status",
                "Remark", "Requested By", "Approved/Rejected By", "Created Time"
        };

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(bos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A3.rotate());

            addTitle(doc, "Approvals Report");

            Table table = new Table(UnitValue.createPercentArray(headers.length))
                    .useAllAvailableWidth();
            addHeaderRow(table, headers);

            for (DetailedCardRequestResponse r : approvals) {
                table.addCell(cell(str(r.getRequestId())));
                table.addCell(cell(r.getMaskedCardNumber()));
                table.addCell(cell(r.getRequestReasonDescription()));
                table.addCell(cell(r.getRequestStatusDescription()));
                table.addCell(cell(r.getRemark()));
                table.addCell(cell(r.getRequestedUser()));
                table.addCell(cell(r.getApprovedUser()));
                table.addCell(cell(r.getCreatedTime() != null ? r.getCreatedTime().format(DT_FMT) : ""));
            }

            doc.add(table);
            doc.close();
            return bos.toByteArray();
        }
    }

    @Override
    public byte[] generateApprovalsCsv() throws IOException {
        List<DetailedCardRequestResponse> approvals = getApprovals();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                CSVWriter csv = new CSVWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {

            csv.writeNext(new String[] {
                    "Request ID", "Masked Card No.", "Reason", "Status",
                    "Remark", "Requested By", "Approved/Rejected By", "Created Time"
            });

            for (DetailedCardRequestResponse r : approvals) {
                csv.writeNext(new String[] {
                        str(r.getRequestId()),
                        r.getMaskedCardNumber(),
                        r.getRequestReasonDescription(),
                        r.getRequestStatusDescription(),
                        r.getRemark(),
                        r.getRequestedUser(),
                        r.getApprovedUser(),
                        r.getCreatedTime() != null ? r.getCreatedTime().format(DT_FMT) : ""
                });
            }
            csv.flush();
            return bos.toByteArray();
        }
    }

    // ─────────────────────────── HELPERS ───────────────────────────

    private List<DetailedCardRequestResponse> getApprovals() {
        return cardRequestService.getAllRequestsWithDetails().stream()
                .filter(r -> "APPR".equals(r.getRequestStatusCode()) || "RJCT".equals(r.getRequestStatusCode()))
                .toList();
    }

    private void addTitle(Document doc, String title) {
        doc.add(new Paragraph(title)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(12));
    }

    private void addHeaderRow(Table table, String[] headers) {
        for (String h : headers) {
            table.addHeaderCell(
                    new Cell().add(new Paragraph(h).setBold())
                            .setBackgroundColor(ColorConstants.DARK_GRAY)
                            .setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(6));
        }
    }

    private Cell cell(String value) {
        return new Cell()
                .add(new Paragraph(value != null ? value : ""))
                .setPadding(4);
    }

    private String str(Object value) {
        return value != null ? value.toString() : "";
    }

    @Override
    public byte[] generateAuditPdf() throws IOException {
        List<AuditLog> logs = auditLogRepo.findAll();

        String[] headers = {
                "Log ID", "Activity Type", "Description", "Performed By", "Timestamp"
        };

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(bos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A3.rotate());

            addTitle(doc, "System Audit Report");

            Table table = new Table(UnitValue.createPercentArray(new float[] { 5, 20, 45, 15, 15 }))
                    .useAllAvailableWidth();
            addHeaderRow(table, headers);

            for (AuditLog log : logs) {
                table.addCell(cell(String.valueOf(log.getLogId())));
                table.addCell(cell(log.getActivityType()));
                table.addCell(cell(log.getDescription()));
                table.addCell(cell(log.getPerformUser()));
                table.addCell(cell(log.getLogTime().format(DT_FMT)));
            }

            doc.add(table);
            doc.close();
            return bos.toByteArray();
        }
    }

    @Override
    public byte[] generateAuditCsv() throws IOException {
        List<AuditLog> logs = auditLogRepo.findAll();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
                CSVWriter writer = new CSVWriter(osw)) {

            writer.writeNext(new String[] { "Log ID", "Activity Type", "Description", "Performed By", "Timestamp" });

            for (AuditLog log : logs) {
                writer.writeNext(new String[] {
                        String.valueOf(log.getLogId()),
                        log.getActivityType(),
                        log.getDescription(),
                        log.getPerformUser(),
                        log.getLogTime().format(DT_FMT)
                });
            }

            writer.flush();
            osw.flush();
            return bos.toByteArray();
        }
    }
}
