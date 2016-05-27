package uk.gov.bis.lite.ogel.database.exception;

public class OgelNotFoundException extends RuntimeException {
  public OgelNotFoundException(String ogelID) {
    super("No Ogel Found With Given Ogel ID: " + ogelID);
  }

}
