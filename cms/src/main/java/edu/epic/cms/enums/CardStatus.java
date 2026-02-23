package edu.epic.cms.enums;

/**
 * Enumeration for card status codes.
 * Maps to CardStatus lookup table in database.
 */
public enum CardStatus {
    IACT("Card Inactive - Initial/Pending state"),
    CACT("Card Active - Normal active state"),
    DACT("Card Deactivated - Card has been deactivated");

    private final String description;

    CardStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return this.name();
    }
}
