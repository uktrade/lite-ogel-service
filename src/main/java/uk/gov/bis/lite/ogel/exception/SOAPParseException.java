package uk.gov.bis.lite.ogel.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class SOAPParseException extends RuntimeException {

  public SOAPParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public static class SOAPParseExceptionHandler implements ExceptionMapper<OgelNotFoundException> {

    @Override
    public Response toResponse(OgelNotFoundException exception) {
      return Response.status(500).entity(exception.getMessage()).type("text/plain").build();
    }
  }
}
