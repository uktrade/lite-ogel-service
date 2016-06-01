package uk.gov.bis.lite.ogel.database.exception;

public class SOAPParseException extends RuntimeException {

  public SOAPParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public SOAPParseException(Throwable cause) {
    super(cause);
  }
}
