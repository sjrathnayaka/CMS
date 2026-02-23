package edu.epic.cms.exception;

public class CardNotFoundException extends RuntimeException {
    
    public CardNotFoundException(String message) {
        super(message);
    }
    
    public CardNotFoundException(Long id) {
        super("Card not found with id: " + id);
    }
    
    public CardNotFoundException(String field, String value) {
        super("Card not found with " + field + ": " + value);
    }
}
