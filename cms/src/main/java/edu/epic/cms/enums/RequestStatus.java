package edu.epic.cms.enums;

/**
 * Enumeration for card request status codes.
 * Maps to RequestStatus lookup table in database.
 */
public enum RequestStatus {
    PEND("Pending"),
    APPR("Approved"),
    RJCT("Rejected");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return this.name();
    }
}
