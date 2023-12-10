package org.roombooking.service.exceptions;

public class UserCreateException extends RuntimeException {
  public UserCreateException(String message, RuntimeException e) {
    super(message, e);
  }
}
