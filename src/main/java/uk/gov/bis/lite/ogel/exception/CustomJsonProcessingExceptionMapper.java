package uk.gov.bis.lite.ogel.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CustomJsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomJsonProcessingExceptionMapper.class);

  @Override
  public Response toResponse(JsonProcessingException exception) {
    LOGGER.debug("Unable to process JSON ", exception);
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(400, exception.getMessage()))
        .build();
  }
}
