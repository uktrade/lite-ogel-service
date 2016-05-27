package uk.gov.bis.lite.ogel.database.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class OgelNotFoundExceptionHandler implements ExceptionMapper<OgelNotFoundException> {

  @Override
  public Response toResponse(OgelNotFoundException exception) {
    return Response.status(404).entity(exception.getMessage()).type("text/plain").build();
  }
}

