package uk.gov.bis.lite.ogel.database.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class LocalOgelNotFoundExceptionHandler implements ExceptionMapper<LocalOgelNotFoundException> {

  @Override
  public Response toResponse(LocalOgelNotFoundException exception) {
    return Response.status(500).entity(exception.getMessage()).type("text/plain").build();
  }
}
