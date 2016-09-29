package uk.gov.bis.lite.ogel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpireOgelFilterTest1 {

  private SpireOgel ogelX;
  private SpireOgel ogelY;
  private SpireOgel ogelZ;
  private List<SpireOgel> standardOgels;

  @Before
  public void setUp() {
    this.ogelX = TestUtil.ogelX();
    this.ogelY = TestUtil.ogelY();
    this.ogelZ = TestUtil.ogelZ();
    this.standardOgels = Arrays.asList(this.ogelX, this.ogelY, this.ogelZ);
  }

  @Test
  public void emptyList() {
    final List<SpireOgel> empty = SpireOgelFilter.filterSpireOgels(TestUtil.list(), "", TestUtil.list(), TestUtil.list());
    assertTrue(empty.isEmpty());
  }

  @Test
  public void filterExcludedCountry() {
    List<SpireOgel> filtered = SpireOgelFilter.filterSpireOgels(standardOgels, TestUtil.standardRatingCode(),
        TestUtil.standardExcludedCountryIds(), TestUtil.getStandardActivityTypes());
    assertNotNull(filtered);
    assertTrue(filtered.isEmpty());
  }

  @Test
  public void filterRating() {
    List<SpireOgel> filterRat1 = SpireOgelFilter.filterSpireOgels(standardOgels, TestUtil.RAT1,
        TestUtil.standardCountryIds(), TestUtil.getStandardActivityTypes());
    assertNotNull(filterRat1);
    assertEquals(3, filterRat1.size());

    List<SpireOgel> filterRat2 = SpireOgelFilter.filterSpireOgels(standardOgels, TestUtil.RAT2,
        TestUtil.standardCountryIds(), TestUtil.getStandardActivityTypes());
    assertNotNull(filterRat2);
    assertEquals(1, filterRat2.size());

    List<SpireOgel> filterRat5 = SpireOgelFilter.filterSpireOgels(standardOgels, TestUtil.RAT5,
        TestUtil.standardCountryIds(), TestUtil.getStandardActivityTypes());
    assertNotNull(filterRat5);
    assertEquals(2, filterRat5.size());
  }

}
