package uk.gov.bis.lite.ogel.exception;

import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CacheNotPopulatedException extends RuntimeException {
  public CacheNotPopulatedException(String message) {
    super(message);
  }

  public static class CacheNotPopulatedExceptionHandler implements ExceptionMapper<CacheNotPopulatedException> {
    @Override
    public Response toResponse(CacheNotPopulatedException exception) {
      return Response.status(500).entity(new ErrorMessage(500, exception.getMessage())).build();
    }
  }
}
