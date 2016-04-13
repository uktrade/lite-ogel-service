package com.bis.lite.ogel.service;

import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.SpireOgel;
import com.bis.lite.ogel.util.SpireOgelTestUtility;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SpireOgelFilterTest {

    SpireOgel firstOgel;

    @Before
    public void setUp(){
        Country firstAllowedCountry = new Country("id1", "TR", "Turkey" );
        Country secondAllowedCountry = new Country("id2", "DE", "Germany" );
        List<Country> allowedCountries = Arrays.asList(firstAllowedCountry, secondAllowedCountry);
        Country firstBannedCountry = new Country("id3", "AF", "Afghanistan" );
        Country secondBannedCountry = new Country("id4", "SY", "Syria" );
        Country thirdBannedCountry = new Country("id5", "NZ", "New Zealand" );
        List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry, thirdBannedCountry);

        firstOgel = SpireOgelTestUtility.createOgel("OGL0", "description", allowedCountries, bannedCountries);
    }

    @Test
    public void returnsEmptyListForEmptyList(){
        final List<SpireOgel> spireOgelsEmpty = SpireOgelFilter.filterSpireOgels(new ArrayList<>(), "", "");
        assertTrue(spireOgelsEmpty.isEmpty());
    }

    @Test
    public void filtersExcludedCountryCorrectly(){
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "OGL0", "id5");
        assertNotNull(filteredOgels);
        assertTrue(filteredOgels.isEmpty());

    }

    @Test
    public void filtersAndFindsOgelsCorrectly(){
        List<SpireOgel> ogelList = Arrays.asList(firstOgel);
        final List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "OGL0", "id6");
        assertNotNull(filteredOgels);
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getOgelCode(), "OGL0");
        assertTrue(filteredOgels.get(0).getExcludedCountries().contains(new Country("id4", "SY", "Syria" )));

    }

    @Test
    public void getsCorrectValuesForMultipleOgelList(){
        Country secondAllowedCountry = new Country("id2", "DE", "Germany" );
        List<Country> allowedCountries = Arrays.asList(secondAllowedCountry);
        Country firstBannedCountry = new Country("id3", "SC", "Some Country" );
        Country secondBannedCountry = new Country("id4", "SOC", "Some Other Country" );
        List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry);
        SpireOgel secondOgel = SpireOgelTestUtility.createOgel("OGL1", "Fire Arms", allowedCountries, bannedCountries);

        List<SpireOgel> ogelList = Arrays.asList(firstOgel, secondOgel);
        List<SpireOgel> filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "OGL5", "id6");
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "OGL1", "id3");
        assertTrue(filteredOgels.isEmpty());

        filteredOgels = SpireOgelFilter.filterSpireOgels(ogelList, "OGL1", "invalidId");
        assertFalse(filteredOgels.isEmpty());
        assertEquals(filteredOgels.get(0).getOgelCode(), "OGL1");
        assertEquals(filteredOgels.get(0).getDescription(), "Fire Arms");

    }
}
