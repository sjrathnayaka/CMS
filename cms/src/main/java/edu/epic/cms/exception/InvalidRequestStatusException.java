package edu.epic.cms.exception;

/**
 * Exception thrown when attempting to approve/reject a request with invalid status.
 */
public class InvalidRequestStatusException extends RuntimeException {
    
    public InvalidRequestStatusException(String message) {
        super(message);
    }
}
