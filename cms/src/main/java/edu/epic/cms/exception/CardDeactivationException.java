package edu.epic.cms.exception;

/**
 * Exception thrown when card deactivation request fails validation.
 */
public class CardDeactivationException extends RuntimeException {
    
    public CardDeactivationException(String message) {
        super(message);
    }
}
