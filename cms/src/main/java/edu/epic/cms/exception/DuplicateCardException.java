package edu.epic.cms.exception;

public class DuplicateCardException extends RuntimeException {
    
    public DuplicateCardException(String cardNumber) {
        super("Card with number " + cardNumber + " already exists");
    }
}
