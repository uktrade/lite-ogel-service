package uk.gov.bis.lite.ogel.resource;

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
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class ApplicableOgelResourceTest {

  private static final SpireOgelService spireOgelService = Mockito.mock(SpireOgelService.class);
  private static final LocalOgelService localOgelService = Mockito.mock(LocalOgelService.class);

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
    reset(spireOgelService);
    reset(localOgelService);
  }

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new ApplicableOgelResource(spireOgelService, localOgelService))
      .build();

  @Test
  public void goodRequestWithResults() {
    when(spireOgelService.findOgel(anyString(), any(), anyListOf(ActivityType.class))).thenReturn(ogels);
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
    when(spireOgelService.findOgel(anyString(), any(), anyListOf(ActivityType.class))).thenReturn(Collections.emptyList());
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

  public static <T> List<String> any() {
    return Arrays.asList(anyString());
  }

  private List<SpireOgel> getEntityOgels(Response response) {
    return (List<SpireOgel>) response.readEntity(List.class);
  }
}
