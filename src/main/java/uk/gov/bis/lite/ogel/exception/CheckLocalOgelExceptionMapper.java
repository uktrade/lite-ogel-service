package uk.gov.bis.lite.ogel.exception;

import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;


public class CheckLocalOgelExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CheckLocalOgelExceptionMapper.class);

  @Override
  public Response toResponse(ConstraintViolationException exception) {

    LOGGER.debug("Unable to process JSON ", exception);
    StringJoiner errorMessageSJ = new StringJoiner(",");
    for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
      errorMessageSJ.add(violation.getMessage());
    }
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(400, errorMessageSJ.toString()))
        .build();
  }

}
