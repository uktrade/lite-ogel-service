package uk.gov.bis.lite.ogel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.util.SpireOgelTestUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilterTest {

  SpireOgel firstOgel;

  @Before
  public void setUp() {
    Country firstAllowedCountry = new Country("id1", "TR", "Turkey");
    Country secondAllowedCountry = new Country("id2", "DE", "Germany");
    List<Country> allowedCountries = Arrays.asList(firstAllowedCountry, secondAllowedCountry);
    Country firstBannedCountry = new Country("id3", "AF", "Afghanistan");
    Country secondBannedCountry = new Country("id4", "SY", "Syria");
    Country thirdBannedCountry = new Country("id5", "NZ", "New Zealand");
    List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry, thirdBannedCountry);

    List<Rating> ratings = new ArrayList<>();
    ratings.add(SpireOgelTestUtility.createRating("ML21a", true));
    ratings.add(SpireOgelTestUtility.createRating("ML21b1", true));
    ratings.add(SpireOgelTestUtility.createRating("ML21b2", false));
    ratings.add(SpireOgelTestUtility.createRating("ML21b3", false));

    OgelCondition ogelCondition = SpireOgelTestUtility.createCondition(ratings, bannedCountries, allowedCountries);
    List<OgelCondition> conditionsList = Arrays.asList(ogelCondition);

    firstOgel = SpireOgelTestUtility.createOgel("OGL0", "description", conditionsList, ActivityType.TECH);
  }

  @Test
  public void returnsEmptyListForEmptyList() {
    final List<SpireOgel> spireOgelsEmpty = SpireOgelFilter.filterSpireOgels(new ArrayList<>(), "",
        new ArrayList<>(), new ArrayList<>());
    assertTrue(spireOgelsEmpty.isEmpty());
  }

  @Test
  public void filtersExcludedCountryCorrectly() {
    List<SpireOgel> ogelList = Collections.singletonList(firstOgel);
    final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", Arrays.asList("id2", "id5"),
        Collections.singletonList(ActivityType.TECH));
    assertNotNull(filteredOgels);
    assertTrue(filteredOgels.isEmpty());
  }

  @Test
  public void filtersAndFindsOgelsCorrectly() {
    List<SpireOgel> ogelList = Collections.singletonList(firstOgel);
    final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", Arrays.asList("id1","id2"), Collections.singletonList(ActivityType.TECH));
    assertNotNull(filteredOgels);
    assertFalse(filteredOgels.isEmpty());
    assertEquals(filteredOgels.get(0).getId(), "OGL0");
    final OgelCondition ogelCondition = filteredOgels.get(0).getOgelConditions().get(0);
    assertTrue(ogelCondition.getExcludedCountries().contains(new Country("id4", "SY", "Syria")));
  }

  @Test
  public void getsCorrectOgelsForMultipleOgels() {
    Country secondAllowedCountry = new Country("id2", "DE", "Germany");
    List<Country> allowedCountries = Collections.singletonList(secondAllowedCountry);
    Country firstBannedCountry = new Country("id3", "SC", "Some Country");
    Country secondBannedCountry = new Country("id4", "SOC", "Some Other Country");
    List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry);
    List<Rating> defaultRatings = Arrays.asList(new Rating("ML4a", true), new Rating("ML4b1", true), new Rating("ML4b2", false));

    OgelCondition ogelCondition = SpireOgelTestUtility.createCondition(defaultRatings, bannedCountries, allowedCountries);
    SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", Collections.singletonList(ogelCondition), ActivityType.TECH);

    List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
    List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", Arrays.asList("id4"), Collections.singletonList(ActivityType.TECH));
    assertTrue(filteredOgels.isEmpty());

    filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", Arrays.asList("id3"), Collections.singletonList(ActivityType.TECH));
    assertTrue(filteredOgels.isEmpty());

    filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", Arrays.asList("id2"), Collections.singletonList(ActivityType.TECH));
    assertFalse(filteredOgels.isEmpty());
    assertEquals(filteredOgels.get(0).getId(), "OGL1");
    assertEquals(filteredOgels.get(0).getName(), "Fire Arms");
  }

  @Test
  public void getsCorrectMultipleOgelsForSameRating() {
    Country secondAllowedCountry = new Country("id2", "DE", "Germany");
    List<Country> allowedCountries = Arrays.asList(secondAllowedCountry);
    Country firstBannedCountry = new Country("id3", "SC", "Some Country");
    Country secondBannedCountry = new Country("id4", "SOC", "Some Other Country");
    List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry);
    List<Rating> ratings = new ArrayList<>();
    ratings.add(SpireOgelTestUtility.createRating("ML4a", true));
    ratings.add(SpireOgelTestUtility.createRating("ML4b1", true));
    ratings.add(SpireOgelTestUtility.createRating("ML4b2", false));
    ratings.add(SpireOgelTestUtility.createRating("ML21b3", false));

    OgelCondition condition = new OgelCondition();
    condition.setRatingList(ratings);
    condition.setIncludedCountries(allowedCountries);
    condition.setExcludedCountries(bannedCountries);
    SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", Arrays.asList(condition), ActivityType.TECH);
    List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
    List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b3", Arrays.asList("id2"), Collections.singletonList(ActivityType.TECH));
    assertEquals(2, filteredOgels.size());
  }

  @Test
  public void getsIncludedCountriesCorrectly() {
    Country firstAllowedCountry = new Country("id9", "US", "United States");
    Country secondAllowedCountry = new Country("id2", "DE", "Germany");
    Country thirdAllowedCountry = new Country("id31", "CH", "China");
    List<Country> allowedCountries = Arrays.asList(firstAllowedCountry, secondAllowedCountry, thirdAllowedCountry);
    List<Rating> defaultRatings = Arrays.asList(new Rating("ML4a", true), new Rating("ML4b1", true), new Rating("ML4b2", false), new Rating("ML5a", false));
    final OgelCondition condition = SpireOgelTestUtility.createCondition(defaultRatings, Collections.emptyList(), allowedCountries);
    SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", Arrays.asList(condition), ActivityType.TECH);
    List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);

    List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", Arrays.asList("id31"), Collections.singletonList(ActivityType.TECH));
    assertFalse(filteredOgels.isEmpty());
    assertEquals(filteredOgels.get(0).getId(), "OGL1");
    final OgelCondition ogelCondition = filteredOgels.get(0).getOgelConditions().get(0);
    final List<String> ratingsGathered = ogelCondition.getRatingList().stream().map(Rating::getRatingCode).collect(Collectors.toList());
    assertTrue(ratingsGathered.contains("ML4a"));
    assertTrue(ratingsGathered.contains("ML4b2"));
    assertEquals(4, ratingsGathered.size());
    assertTrue(ogelCondition.getIncludedCountries().contains(firstAllowedCountry));
    assertTrue(ogelCondition.getIncludedCountries().contains(secondAllowedCountry));

    List<SpireOgel> secondList = SpireOgelFilter.filterSpireOgels(ogelList, "ML4c", Arrays.asList("CountryNotInIncludedList"), Collections.singletonList(ActivityType.TECH));
    assertTrue(secondList.isEmpty());
  }

  @Test
  public void returnsOgelsCorrectlyForDifferentCategories() {
    List<SpireOgel> ogelList = Arrays.asList(firstOgel);
    List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", Arrays.asList("id1"), Arrays.asList(ActivityType.MIL_GOV));
    assertNotNull(filteredOgels);
    assertTrue(filteredOgels.isEmpty());

    filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1",
        Arrays.asList("id1"), Arrays.asList(ActivityType.TECH, ActivityType.MIL_GOV));
    assertEquals(1, filteredOgels.size());
  }
}
