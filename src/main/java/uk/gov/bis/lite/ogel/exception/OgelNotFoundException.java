package uk.gov.bis.lite.ogel.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class OgelNotFoundException extends WebApplicationException {

  public OgelNotFoundException(String ogelID) {
    super("No Ogel Found With Given Ogel ID: " + ogelID, Response.Status.NOT_FOUND);
  }
}
