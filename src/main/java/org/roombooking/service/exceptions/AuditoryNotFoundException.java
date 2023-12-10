package org.roombooking.service.exceptions;

public class AuditoryNotFoundException extends RuntimeException {
  public AuditoryNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
