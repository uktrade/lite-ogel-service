package uk.gov.bis.lite.ogel.model;

public enum ActivityType {
  TECH("TECH"),
  MIL_GOV("MIL_GOV"),
  MIL_ANY("MIL_ANY"),
  EXHIBITION("EXHIBITION"),
  REPAIR("REPAIR"),
  DU_ANY("DU_ANY");

  private final String name;

  ActivityType(String name) {
    this.name = name;
  }

  public static ActivityType fromName(String name) {

    for (ActivityType activityType : ActivityType.values()) {
      if (name.equals(activityType.name)) {
        return activityType;
      }
    }

    return null;
  }

  public static boolean typeExists(String name) {
    return fromName(name) != null;
  }

}
