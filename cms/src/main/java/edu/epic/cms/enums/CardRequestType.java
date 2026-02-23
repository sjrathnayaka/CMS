package edu.epic.cms.enums;

/**
 * Enumeration for card request type codes.
 * Maps to CardRequestType lookup table in database.
 */
public enum CardRequestType {
    ACTI("Card Activation Request"),
    CDCL("Card Close Request");

    private final String description;

    CardRequestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return this.name();
    }
}
