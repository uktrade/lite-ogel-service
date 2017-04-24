package uk.gov.bis.lite.ogel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApplicableOgelServiceTest {

  private SpireOgelService mockedSpireOgelService = mock(SpireOgelService.class);
  private ApplicableOgelService applicableOgelService;

  @Before
  public void setUp() {
    List<SpireOgel> spireOgels = Arrays.asList(TestUtil.ogelX(), TestUtil.ogelY(), TestUtil.ogelZ(), TestUtil.ogelMix());
    when(mockedSpireOgelService.getAllOgels()).thenReturn(spireOgels);
    applicableOgelService = new ApplicableOgelServiceImpl(mockedSpireOgelService);
  }

  @Test
  public void emptyList() {
    when(mockedSpireOgelService.getAllOgels()).thenReturn(Collections.emptyList());
    List<SpireOgel> empty = applicableOgelService.findOgel("", Collections.emptyList(), Collections.emptyList());
    assertTrue(empty.isEmpty());
  }

  @Test
  public void filterByCountry() {
    List<SpireOgel> filter1 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(4), TestUtil.activities());
    assertNotNull(filter1);
    assertTrue(filter1.isEmpty());

    List<SpireOgel> filter2 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(3), TestUtil.activities());
    assertThat(filter2).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLX);

    List<SpireOgel> filter3 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(2), TestUtil.activities());
    assertThat(filter3).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLX, TestUtil.OGLY);
  }

  @Test
  public void filterByRating() {
    List<SpireOgel> filter1 = applicableOgelService.findOgel(TestUtil.RATX, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertNotNull(filter1);
    assertEquals(0, filter1.size());

    List<SpireOgel> filter2 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(1, 2), TestUtil.activities());
    assertThat(filter2).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLX, TestUtil.OGLY);

    List<SpireOgel> filter3 = applicableOgelService.findOgel(TestUtil.RAT3, TestUtil.countryIds(1, 2, 3), TestUtil.activities());
    assertThat(filter3).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLX);

    List<SpireOgel> filter4 = applicableOgelService.findOgel(TestUtil.RAT5, TestUtil.countryIds(1), TestUtil.activities());
    assertThat(filter4).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLY, TestUtil.OGLZ);
  }

  @Test
  public void filterByRatingAndCountry() {
    List<SpireOgel> filter1 = applicableOgelService.findOgel(TestUtil.RAT5, TestUtil.countryIds(2), TestUtil.activities());
    assertThat(filter1).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLY);

    List<SpireOgel> filter2 = applicableOgelService.findOgel(TestUtil.RAT5, TestUtil.countryIds(1), TestUtil.activities());
    assertThat(filter2).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLY, TestUtil.OGLZ);

    List<SpireOgel> filter3 = applicableOgelService.findOgel(TestUtil.RAT2, TestUtil.countryIds(2, 3), TestUtil.activities());
    assertThat(filter3).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLX);

    List<SpireOgel> filter4 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(4), TestUtil.activities());
    assertEquals(0, filter4.size());
  }

  @Test
  public void filterByActivityType() {
    List<SpireOgel> filter1 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(1), TestUtil.tech());
    assertThat(filter1).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLX, TestUtil.OGLZ);

    List<SpireOgel> filter2 = applicableOgelService.findOgel(TestUtil.RAT1, TestUtil.countryIds(1), TestUtil.repair());
    assertThat(filter2).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLY);
  }

  @Test
  public void filterByActivityTypeRatingCountry() {
    List<SpireOgel> filter1 = applicableOgelService.findOgel(TestUtil.RAT4, TestUtil.countryIds(1, 2, 3), TestUtil.tech());
    assertEquals(0, filter1.size());

    List<SpireOgel> filter2 = applicableOgelService.findOgel(TestUtil.RAT4, TestUtil.countryIds(2), TestUtil.repair());
    assertThat(filter2).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLY);

    List<SpireOgel> filter3 = applicableOgelService.findOgel(TestUtil.RAT4, TestUtil.countryIds(3), TestUtil.repair());
    assertEquals(0, filter3.size());
  }

  /**
   * Rating/country matches apply to the same condition, this test checks that no match is found when
   * rating and country are in different conditions for same Ogel
   */
  @Test
  public void filterConditionMix() {
    List<SpireOgel> filter1 = applicableOgelService.findOgel(TestUtil.RATA, TestUtil.countryIds(6), TestUtil.activities());
    assertEquals(0, filter1.size());

    List<SpireOgel> filter2 = applicableOgelService.findOgel(TestUtil.RATA, TestUtil.countryIds(5), TestUtil.activities());
    assertThat(filter2).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLMIX);

    List<SpireOgel> filter3 = applicableOgelService.findOgel(TestUtil.RATB, TestUtil.countryIds(6), TestUtil.activities());
    assertThat(filter3).extracting(SpireOgel::getId).containsOnly(TestUtil.OGLMIX);

    List<SpireOgel> filter4 = applicableOgelService.findOgel(TestUtil.RATB, TestUtil.countryIds(5), TestUtil.activities());
    assertEquals(0, filter4.size());
  }

}
