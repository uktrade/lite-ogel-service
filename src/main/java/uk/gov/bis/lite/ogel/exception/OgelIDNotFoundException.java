package uk.gov.bis.lite.ogel.exception;

import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class OgelIDNotFoundException extends RuntimeException {
  public OgelIDNotFoundException() {
    super("ID not found for the Ogel being updated.");
  }

  public static class OgelIDNotFoundExceptionHandler implements ExceptionMapper<OgelIDNotFoundException> {
    @Override
    public Response toResponse(OgelIDNotFoundException exception) {
      return Response.status(404).entity(new ErrorMessage(404, exception.getMessage())).build();
    }
  }
}
