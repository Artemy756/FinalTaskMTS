package org.roombooking.service.exceptions;

public class BookException extends RuntimeException {
    public BookException(String message, Throwable cause) {
        super(message, cause);
    }
}
