package org.roombooking.service.exceptions;

public class DeleteException extends RuntimeException {
    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
