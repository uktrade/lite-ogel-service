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
    return type;
  }

  public static ConditionType fromString(String str) {
    for (ConditionType conditionType : values()) {
      if (conditionType.toString().equals(str)) {
        return conditionType;
      }
    }
    return null;
  }

}
