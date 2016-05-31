package uk.gov.bis.lite.ogel.database.exception;

public class LocalOgelNotFoundException extends RuntimeException {

  public LocalOgelNotFoundException(String ogelID) {
    super("An unexpected error occurred. Failed to find local OGEL entry with ID: " + ogelID);
  }
}