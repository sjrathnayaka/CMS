package edu.epic.cms.exception;

/**
 * Exception thrown when a card request is not found.
 */
public class RequestNotFoundException extends RuntimeException {
    
    public RequestNotFoundException(Long requestId) {
        super("Request not found with ID: " + requestId);
    }
    
    public RequestNotFoundException(String message) {
        super(message);
    }
}
