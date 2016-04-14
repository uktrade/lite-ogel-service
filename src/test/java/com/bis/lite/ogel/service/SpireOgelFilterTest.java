package com.bis.lite.ogel.service;

import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.SpireOgel;
import com.bis.lite.ogel.util.SpireOgelTestUtility;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<String> defaultRatings = Arrays.asList("ML21a", "ML21b1", "ML21b2", "ML21b3");

        firstOgel = SpireOgelTestUtility.createOgel("OGL0", "description", defaultRatings, allowedCountries, bannedCountries);
    }

    @Test
    public void returnsEmptyListForEmptyList() {
        final List<SpireOgel> spireOgelsEmpty = SpireOgelFilter.filterSpireOgels(new ArrayList<>(), "", "");
        assertTrue(spireOgelsEmpty.isEmpty());
    }

    @Test
    public void filtersExcludedCountryCorrectly() {
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", "id5");
        assertNotNull(filteredOgels);
        assertTrue(filteredOgels.isEmpty());

    }

    @Test
    public void filtersAndFindsOgelsCorrectly() {
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b1", "someCountry");
        assertNotNull(filteredOgels);
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getOgelCode(), "OGL0");
        assertTrue(filteredOgels.get(0).getExcludedCountries().contains(new Country("id4", "SY", "Syria")));

    }

    @Test
    public void getsCorrectOgelsForMultipleOgels() {
        Country secondAllowedCountry = new Country("id2", "DE", "Germany");
        List<Country> allowedCountries = Arrays.asList(secondAllowedCountry);
        Country firstBannedCountry = new Country("id3", "SC", "Some Country");
        Country secondBannedCountry = new Country("id4", "SOC", "Some Other Country");
        List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry);
        List<String> defaultRatings = Arrays.asList("ML4a", "ML4b1", "ML4b2", "ML4c", "ML5a");
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", defaultRatings, allowedCountries, bannedCountries);

        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "id4");
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "id3");
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "invalidId");
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getOgelCode(), "OGL1");
        assertEquals(filteredOgels.get(0).getDescription(), "Fire Arms");
    }

    @Test
    public void getsCorrectMultipleOgelsForSameRating() {
        Country secondAllowedCountry = new Country("id2", "DE", "Germany");
        List<Country> allowedCountries = Arrays.asList(secondAllowedCountry);
        Country firstBannedCountry = new Country("id3", "SC", "Some Country");
        Country secondBannedCountry = new Country("id4", "SOC", "Some Other Country");
        List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry);
        List<String> defaultRatings = Arrays.asList("ML4a", "ML4b1", "ML4b2", "ML4c", "ML5a", "ML21b3");
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", defaultRatings, allowedCountries, bannedCountries);

        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML21b3", "invalidCountry");
        assertEquals(2, filteredOgels.size());
    }

    @Test
    public void getsIncludedCountriesCorrectly() {
        Country firstAllowedCountry = new Country("id9", "US", "United States");
        Country secondAllowedCountry = new Country("id2", "DE", "Germany");
        Country thirdAllowedCountry = new Country("id31", "CH", "China");
        List<Country> allowedCountries = Arrays.asList(firstAllowedCountry, secondAllowedCountry, thirdAllowedCountry);
        List<String> defaultRatings = Arrays.asList("ML4a", "ML4b1", "ML4b2", "ML4c", "ML5a", "ML21b3");
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", defaultRatings, allowedCountries, new ArrayList<>());
        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);

        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "ML4b1", "id31");
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getOgelCode(), "OGL1");
        assertTrue(filteredOgels.get(0).getRatingCodes().contains("ML4a"));
        assertTrue(filteredOgels.get(0).getRatingCodes().contains("ML4b2"));
        assertTrue(filteredOgels.get(0).getIncludedCountries().contains(firstAllowedCountry));
        assertTrue(filteredOgels.get(0).getIncludedCountries().contains(secondAllowedCountry));

        List<SpireOgel> secondList = SpireOgelFilter.filterSpireOgels(ogelList, "ML4c", "CountryNotInIncludedList");
        assertTrue(secondList.isEmpty());

    }
}
