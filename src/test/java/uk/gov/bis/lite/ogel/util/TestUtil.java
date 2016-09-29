package uk.gov.bis.lite.ogel.util;

import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtil {

  public static String OGLX = "OGLX";
  public static String OGLY = "OGLY";
  public static String OGLZ = "OGLZ";

  public static String COUNTRY1_ID = "1";
  public static String COUNTRY1_SET_ID = "11";
  public static String COUNTRY1_NAME = "Country1";

  public static String COUNTRY2_ID = "2";
  public static String COUNTRY2_SET_ID = "22";
  public static String COUNTRY2_NAME = "Country2";

  public static String COUNTRY3_ID = "3";
  public static String COUNTRY3_SET_ID = "33";
  public static String COUNTRY3_NAME = "Country3";

  public static String COUNTRY4_ID = "4";
  public static String COUNTRY4_SET_ID = "44";
  public static String COUNTRY4_NAME = "Country4";

  private static Map<Integer, Country> countryMap = new HashMap<Integer, Country>();

  static {
    countryMap.put(1, new Country(COUNTRY1_ID, COUNTRY1_SET_ID, COUNTRY1_NAME));
    countryMap.put(2, new Country(COUNTRY2_ID, COUNTRY2_SET_ID, COUNTRY2_NAME));
    countryMap.put(3, new Country(COUNTRY3_ID, COUNTRY3_SET_ID, COUNTRY3_NAME));
    countryMap.put(4, new Country(COUNTRY4_ID, COUNTRY4_SET_ID, COUNTRY4_NAME));
  }

  public static String RAT1 = "rat1";
  public static String RAT2 = "rat2";
  public static String RAT3 = "rat3";
  public static String RAT4 = "rat4";
  public static String RAT5 = "rat5";

  public static SpireOgel ogelX() {
    return createStandardOgel(OGLX, createConditions(ratings(RAT1, RAT2, RAT3),
        countries(1, 2, 3), countries(4)), ActivityType.TECH);
  }

  public static SpireOgel ogelY() {
    return createStandardOgel(OGLY, createConditions(ratings(RAT1, RAT4, RAT5),
        countries(1, 2, 3), countries(4)), ActivityType.TECH);
  }

  public static SpireOgel ogelZ() {
    return createStandardOgel(OGLZ, createConditions(ratings(RAT1, RAT5),
        countries(1, 2, 3), countries(4)), ActivityType.TECH);
  }

  public static SpireOgel createStandardOgel(String code, List<OgelCondition> conditions, ActivityType activity) {
    SpireOgel ogel = new SpireOgel();
    ogel.setOgelConditions(conditions);
    ogel.setId(code);
    ogel.setName("Name" + code);
    ogel.setActivityType(activity);
    return ogel;
  }

  public static List<ActivityType> getStandardActivityTypes() {
    return Collections.singletonList(ActivityType.TECH);
  }

  public static Rating createRating(String code) {
    return new Rating(code);
  }

  public static String standardRatingCode() {
    return RAT1;
  }

  public static <T> List<T> list() {
    return new ArrayList<>();
  }

  public static List<Country> standardExcludedCountries() {
    return Collections.singletonList(new Country(COUNTRY4_ID, COUNTRY4_SET_ID, COUNTRY4_NAME));
  }

  public static List<String> standardExcludedCountryIds() {
    return Collections.singletonList(COUNTRY4_ID);
  }

  public static List<String> standardCountryIds() {
    return new ArrayList<>(Arrays.asList(COUNTRY1_ID, COUNTRY2_ID, COUNTRY3_ID));
  }

  public static List<OgelCondition> createConditions(List<Rating> ratings, List<Country> includedCountries, List<Country> excludedCountries) {
    OgelCondition condition1 = createCondition(ratings, includedCountries, OgelCondition.CountryStatus.INCLUDED);
    OgelCondition condition2 = createCondition(ratings, excludedCountries, OgelCondition.CountryStatus.EXCLUDED);
    return Arrays.asList(condition1, condition2);
  }

  public static OgelCondition createCondition(List<Rating> ratings, List<Country> countries, OgelCondition.CountryStatus status) {
    OgelCondition condition = new OgelCondition();
    condition.setRatingList(ratings);
    condition.setCountries(countries, status);
    return condition;
  }

  private static List<Rating> standardRatings() {
    List<Rating> ratings = new ArrayList<>();
    ratings.add(TestUtil.createRating(RAT1));
    ratings.add(TestUtil.createRating(RAT2));
    ratings.add(TestUtil.createRating(RAT3));
    return ratings;
  }

  private static List<Rating> ratings(String... codes) {
    List<Rating> ratings = new ArrayList<>();
    for(String code : codes){
      ratings.add(new Rating(code));
    }
    return ratings;
  }

  private static List<Country> standardIncludedCountries() {
    Country country1 = new Country(COUNTRY1_ID, COUNTRY1_SET_ID, COUNTRY1_NAME);
    Country country2 = new Country(COUNTRY2_ID, COUNTRY2_SET_ID, COUNTRY2_NAME);
    Country country3 = new Country(COUNTRY3_ID, COUNTRY3_SET_ID, COUNTRY3_NAME);
    return Arrays.asList(country1, country2, country3);
  }

  private static List<Country> countries(int... countryMapKeys) {
    List<Country> countries = new ArrayList<>();
    for(int key : countryMapKeys){
      countries.add(countryMap.get(key));
    }
    return countries;
  }




}
