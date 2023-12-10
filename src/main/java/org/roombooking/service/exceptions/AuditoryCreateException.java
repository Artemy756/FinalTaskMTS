package org.roombooking.service.exceptions;


public class AuditoryCreateException extends RuntimeException {
  public AuditoryCreateException(String message, RuntimeException e) {
    super(message, e);
  }
}
