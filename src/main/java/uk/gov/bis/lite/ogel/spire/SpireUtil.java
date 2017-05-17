package uk.gov.bis.lite.ogel.spire;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SpireUtil {

  private static final String COUNTRY_PREFIX = "CTRY";

  private SpireUtil() {
  }

  /**
   * Removes 'CTRY' from any list item, returns altered list
   */
  public static List<String> stripCountryPrefix(List<String> countries) {
    return countries.stream()
        .map (c -> StringUtils.remove(c, COUNTRY_PREFIX))
        .collect (Collectors.toList());
  }

}
