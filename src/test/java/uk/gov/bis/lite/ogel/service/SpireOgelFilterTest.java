package uk.gov.bis.lite.ogel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.Arrays;
import java.util.List;

public class SpireOgelFilterTest {

  private List<SpireOgel> ogels;

  @Before
  public void setUp() {
    this.ogels = Arrays.asList(TestUtil.ogelX(), TestUtil.ogelY(), TestUtil.ogelZ(), TestUtil.ogelMix());
  }

  @Test
  public void emptyList() {
    final List<SpireOgel> empty = SpireOgelFilter.filterSpireOgels(TestUtil.list(), "", TestUtil.list(), TestUtil.list());
    assertTrue(empty.isEmpty());
  }

  @Test
  public void filterByCountry() {
    List<SpireOgel> filter1 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(4), TestUtil.activities());
    assertNotNull(filter1);
    assertTrue(filter1.isEmpty());

    List<SpireOgel> filter2 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(3), TestUtil.activities());
    assertEquals(1, filter2.size());

    List<SpireOgel> filter3 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(2), TestUtil.activities());
    assertEquals(2, filter3.size());
  }

  @Test
  public void filterByRating() {
    List<SpireOgel> filter1 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RATX, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertNotNull(filter1);
    assertEquals(0, filter1.size());

    List<SpireOgel> filter2 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertEquals(3, filter2.size());

    List<SpireOgel> filter3 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT3, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertEquals(1, filter3.size());

    List<SpireOgel> filter4 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT5, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertEquals(2, filter4.size());
  }

  @Test
  public void filterByRatingAndCountry() {
    List<SpireOgel> filter1 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT5, TestUtil.countryIds(2), TestUtil.activities());
    assertEquals(1, filter1.size());
    assertEquals(TestUtil.OGLY, filter1.get(0).getId());

    List<SpireOgel> filter2 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT5, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertEquals(2, filter2.size());
    assertTrue(TestUtil.OGLY.equals(filter2.get(0).getId()) || TestUtil.OGLZ.equals(filter2.get(0).getId()));
    assertTrue(TestUtil.OGLY.equals(filter2.get(1).getId()) || TestUtil.OGLZ.equals(filter2.get(1).getId()));

    List<SpireOgel> filter3 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT2, TestUtil.countryIds(2, 3), TestUtil.activities());
    assertEquals(2, filter3.size());
    assertTrue(TestUtil.OGLY.equals(filter3.get(0).getId()) || TestUtil.OGLX.equals(filter3.get(0).getId()));
    assertTrue(TestUtil.OGLY.equals(filter3.get(1).getId()) || TestUtil.OGLX.equals(filter3.get(1).getId()));

    List<SpireOgel> filter4 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(4), TestUtil.activities());
    assertEquals(0, filter4.size());
  }

  @Test
  public void filterByActivityType() {
    List<SpireOgel> filter1 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(1, 2, 3), TestUtil.tech());
    assertEquals(2, filter1.size());

    List<SpireOgel> filter2 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT1, TestUtil.countryIds(1, 2, 3), TestUtil.repair());
    assertEquals(1, filter2.size());
  }

  @Test
  public void filterByActivityTypeRatingCountry() {
    List<SpireOgel> filter1 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT4, TestUtil.countryIds(1, 2, 3), TestUtil.tech());
    assertEquals(0, filter1.size());

    List<SpireOgel> filter2 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT4, TestUtil.countryIds(2), TestUtil.repair());
    assertEquals(1, filter2.size());

    List<SpireOgel> filter3 = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RAT4, TestUtil.countryIds(3), TestUtil.repair());
    assertEquals(0, filter3.size());
  }

  @Test
  public void filterConditionMix() {
    List<SpireOgel> filter = SpireOgelFilter.filterSpireOgels(ogels, TestUtil.RATA, TestUtil.countryIds(6), TestUtil.activities());
    assertEquals(0, filter.size());
  }

}
