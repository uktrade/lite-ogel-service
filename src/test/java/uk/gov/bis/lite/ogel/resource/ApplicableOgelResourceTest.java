package uk.gov.bis.lite.ogel.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.ApplicableOgelService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class ApplicableOgelResourceTest {

  private static final LocalOgelService localOgelService = Mockito.mock(LocalOgelService.class);
  private static final ApplicableOgelService applicableOgelService = Mockito.mock(ApplicableOgelService.class);

  private List<SpireOgel> ogels;

  private final String CONTROL_CODE_NAME = "controlCode";
  private final String CONTROL_CODE_PARAM = "CC01";
  private final String SOURCE_COUNTRY_NAME = "sourceCountry";
  private final String SOURCE_COUNTRY_PARAM = "1";
  private final String DEST_COUNTRY_NAME = "destinationCountry";
  private final String DEST1_COUNTRY_PARAM = "2";
  private final String DEST2_COUNTRY_PARAM = "3";
  private final String ACTIVITY_NAME = "activityType";
  private final String ACTIVITY_PARAM = "TECH";

  @Before
  public void setUp() {
    this.ogels = Arrays.asList(TestUtil.ogelX(), TestUtil.ogelY(), TestUtil.ogelZ());
  }

  @After
  public void tearDown(){
    reset(localOgelService);
  }

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new ApplicableOgelResource(applicableOgelService, localOgelService, TestUtil.OGLEU))
      .build();

  @Test
  public void goodRequestWithResults() {
    when(applicableOgelService.findOgel(anyString(), any(), anyListOf(ActivityType.class))).thenReturn(ogels);
    when(localOgelService.findLocalOgelById(anyString())).thenReturn(Optional.empty());

    Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST2_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM)
        .request().get();

    assertEquals(200, response.getStatus());
    assertEquals(3, getEntityOgels(response).size());
  }

  @Test
  public void goodRequestWithNoResults() {
    when(applicableOgelService.findOgel(anyString(), any(), anyListOf(ActivityType.class))).thenReturn(Collections.emptyList());
    final Response response = resources.client()
        .target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST2_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM)
        .request().get();

    assertEquals(200, response.getStatus());
    assertEquals(0, getEntityOgels(response).size());
  }

  @Test
  public void filtersVirtualEuWhenMatched() {
    //If the virtual EU OGEL is matched, the resource should not return it
    List<SpireOgel> ogelsWithVirtualEu = new ArrayList<>(ogels);
    ogelsWithVirtualEu.add(TestUtil.ogelEU());

    when(applicableOgelService.findOgel(anyString(), any(), anyListOf(ActivityType.class))).thenReturn(ogelsWithVirtualEu);
    when(localOgelService.findLocalOgelById(anyString())).thenReturn(Optional.empty());

    Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM)
        .request().get();

    assertEquals(200, response.getStatus());

    List<Map<String, Object>> entityOgels = getEntityOgels(response);

    assertEquals(3, entityOgels.size());
    assertThat(entityOgels).extracting("id").containsOnly(TestUtil.OGLX, TestUtil.OGLY, TestUtil.OGLZ);
  }

  @Test
  public void invalidActivityType() {
    final Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM + "X")
        .request().get();
    assertNotNull(response);
    assertEquals(response.getStatus(), 400);
  }

  @Test
  public void noActivityType() {
    final Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .request().get();
    assertNotNull(response);
    assertEquals(response.getStatus(), 400);
  }

  @Test
  public void noDestinationCountry() {
    final Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM)
        .request().get();
    assertNotNull(response);
    assertEquals(response.getStatus(), 400);
  }

  @Test
  public void orderedByRanking() {
    List<SpireOgel> ogelsByAscendingRanking = Arrays.asList(TestUtil.ogelY(), TestUtil.ogelZ(), TestUtil.ogelX());

    when(applicableOgelService.findOgel(anyString(), anyListOf(String.class), anyListOf(ActivityType.class))).thenReturn(ogelsByAscendingRanking);
    when(localOgelService.findLocalOgelById(anyString())).thenReturn(Optional.empty());

    Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM)
        .request().get();

    assertEquals(200, response.getStatus());

    assertThat(getEntityOgels(response)).extracting("id").containsExactly(TestUtil.OGLX, TestUtil.OGLY, TestUtil.OGLZ);
  }

  @Test
  public void orderedByRankingDuplicateRanking() {
    // Descending by ID alphabetically
    List<SpireOgel> ogelsWithSameRanking = Arrays.asList(TestUtil.ogelX(), TestUtil.ogelW());

    when(applicableOgelService.findOgel(anyString(), any(), anyListOf(ActivityType.class))).thenReturn(ogelsWithSameRanking);
    when(localOgelService.findLocalOgelById(anyString())).thenReturn(Optional.empty());

    Response response = resources.client().target("/applicable-ogels")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DEST_COUNTRY_NAME, DEST1_COUNTRY_PARAM)
        .queryParam(ACTIVITY_NAME, ACTIVITY_PARAM)
        .request().get();

    assertEquals(200, response.getStatus());

    // Ascending by ID alphabetically
    assertThat(getEntityOgels(response)).extracting("id").containsExactly(TestUtil.OGLW, TestUtil.OGLX);
  }

  public static <T> List<String> any() {
    return Arrays.asList(anyString());
  }

  private List<Map<String, Object>> getEntityOgels(Response response) {
    return response.readEntity(new GenericType<List<Map<String, Object>>>(){});
  }
}
