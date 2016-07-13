package uk.gov.bis.lite.ogel.model.localOgel;

public enum ConditionType {

  CAN_LIST("canList"),
  CANT_LIST("cantList"),
  MUST_LIST("mustList"),
  HOW_TO_USE_LIST("howToUseList");

  private final String type;

  ConditionType(final String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return this.type;
  }

  public static ConditionType fromString(String text) {
    if (text != null) {
      for (ConditionType type : ConditionType.values()) {
        if (text.equalsIgnoreCase(type.type)) {
          return type;
        }
      }
    }
    throw new IllegalArgumentException("No condition type " + text + " is found");
  }
}
