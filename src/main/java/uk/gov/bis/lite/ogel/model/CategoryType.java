package uk.gov.bis.lite.ogel.model;

public enum CategoryType {
  TECH("TECH"),
  MIL_GOV("MIL_GOV"),
  MIL_ANY("MIL_ANY"),
  EXHIBITION("EXHIBITION"),
  REPAIR("REPAIR"),
  DU_ANY("DU_ANY");

  private final String name;

  CategoryType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "CategoryType{" +
        "name='" + name + '\'' +
        '}';
  }
}
