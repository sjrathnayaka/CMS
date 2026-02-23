package edu.epic.cms.exception;

/**
 * Exception thrown when card activation request fails validation.
 */
public class CardActivationException extends RuntimeException {
    
    public CardActivationException(String message) {
        super(message);
    }
}
