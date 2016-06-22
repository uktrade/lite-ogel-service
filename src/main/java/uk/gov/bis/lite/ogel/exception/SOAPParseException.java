package uk.gov.bis.lite.ogel.exception;

public class SOAPParseException extends RuntimeException {

  public SOAPParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public SOAPParseException(Throwable cause) {
    super(cause);
  }
}
