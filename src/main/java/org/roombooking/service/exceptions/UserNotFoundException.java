package org.roombooking.service.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
