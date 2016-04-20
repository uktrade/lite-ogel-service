package com.bis.lite.ogel.service;

import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.OgelCondition;
import com.bis.lite.ogel.model.Rating;
import com.bis.lite.ogel.model.SpireOgel;
import com.bis.lite.ogel.util.SpireOgelTestUtility;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

        firstOgel = SpireOgelTestUtility.createOgel("OGL0", "description", conditionsList, CategoryType.TECH);
    }

    @Test
    public void returnsEmptyListForEmptyList() {
        final List<SpireOgel> spireOgelsEmpty = SpireOgelFilter.filterSpireOgels(new ArrayList<>(), "", "", new ArrayList<>());
        assertTrue(spireOgelsEmpty.isEmpty());
    }

    @Test
    public void filtersExcludedCountryCorrectly() {
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", "id5", Arrays.asList(CategoryType.TECH));
        assertNotNull(filteredOgels);
        assertTrue(filteredOgels.isEmpty());
    }

    @Test
    public void filtersAndFindsOgelsCorrectly() {
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", "someCountry", Arrays.asList(CategoryType.TECH));
        assertNotNull(filteredOgels);
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getId(), "OGL0");
        final OgelCondition ogelCondition = filteredOgels.get(0).getOgelConditions().get(0);
        assertTrue(ogelCondition.getExcludedCountries().contains(new Country("id4", "SY", "Syria")));
    }

    @Test
    public void getsCorrectOgelsForMultipleOgels() {
        Country secondAllowedCountry = new Country("id2", "DE", "Germany");
        List<Country> allowedCountries = Arrays.asList(secondAllowedCountry);
        Country firstBannedCountry = new Country("id3", "SC", "Some Country");
        Country secondBannedCountry = new Country("id4", "SOC", "Some Other Country");
        List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry);
        List<Rating> defaultRatings = Arrays.asList(new Rating("ML4a", true), new Rating("ML4b1", true), new Rating("ML4b2",false));

        OgelCondition ogelCondition = SpireOgelTestUtility.createCondition(defaultRatings, bannedCountries, allowedCountries);
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", Arrays.asList(ogelCondition), CategoryType.TECH);

        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "id4", Arrays.asList(CategoryType.TECH));
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "id3", Arrays.asList(CategoryType.TECH));
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "invalidId", Arrays.asList(CategoryType.TECH));
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getId(), "OGL1");
        assertEquals(filteredOgels.get(0).getDescription(), "Fire Arms");
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
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", Arrays.asList(condition), CategoryType.TECH);
        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b3", "invalidCountry", Arrays.asList(CategoryType.TECH));
        assertEquals(2, filteredOgels.size());
    }

    @Test
    public void getsIncludedCountriesCorrectly() {
        Country firstAllowedCountry = new Country("id9", "US", "United States");
        Country secondAllowedCountry = new Country("id2", "DE", "Germany");
        Country thirdAllowedCountry = new Country("id31", "CH", "China");
        List<Country> allowedCountries = Arrays.asList(firstAllowedCountry, secondAllowedCountry, thirdAllowedCountry);
        List<Rating> defaultRatings = Arrays.asList(new Rating("ML4a", true), new Rating("ML4b1", true), new Rating("ML4b2",false), new Rating("ML5a",false));
        final OgelCondition condition = SpireOgelTestUtility.createCondition(defaultRatings, Collections.emptyList(), allowedCountries);
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", Arrays.asList(condition), CategoryType.TECH);
        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);

        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "id31", Arrays.asList(CategoryType.TECH));
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getId(), "OGL1");
        final OgelCondition ogelCondition = filteredOgels.get(0).getOgelConditions().get(0);
        final List<String> ratingsGathered = ogelCondition.getRatingList().stream().map(Rating::getRatingCode).collect(Collectors.toList());
        assertTrue(ratingsGathered.contains("ML4a"));
        assertTrue(ratingsGathered.contains("ML4b2"));
        assertEquals(4, ratingsGathered.size());
        assertTrue(ogelCondition.getIncludedCountries().contains(firstAllowedCountry));
        assertTrue(ogelCondition.getIncludedCountries().contains(secondAllowedCountry));

        List<SpireOgel> secondList = SpireOgelFilter.filterSpireOgels(ogelList, "ML4c", "CountryNotInIncludedList", Arrays.asList(CategoryType.TECH));
        assertTrue(secondList.isEmpty());
    }

    @Test
    public void returnsOgelsCorrectlyForDifferentCategories() {
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", "someCountry", Arrays.asList(CategoryType.MIL_GOV));
        assertNotNull(filteredOgels);
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1",
                "someCountry", Arrays.asList(CategoryType.TECH, CategoryType.MIL_GOV));
        assertEquals(1, filteredOgels.size());
    }
}
