package uk.gov.bis.lite.ogel.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class OgelNotFoundException extends RuntimeException {
  public OgelNotFoundException(String ogelID) {
    super("No Ogel Found With Given Ogel ID: " + ogelID);
  }

  public static class OgelNotFoundExceptionHandler implements ExceptionMapper<OgelNotFoundException> {
    @Override
    public Response toResponse(OgelNotFoundException exception) {
      return Response.status(404).entity(exception.getMessage()).type("text/plain").build();
    }
  }
}