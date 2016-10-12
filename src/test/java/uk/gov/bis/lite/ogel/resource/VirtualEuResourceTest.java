package uk.gov.bis.lite.ogel.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class VirtualEuResourceTest {

  private static final SpireOgelService spireOgelService = Mockito.mock(SpireOgelService.class);
  private static final LocalOgelService localOgelService = Mockito.mock(LocalOgelService.class);

  private List<SpireOgel> euOgels;
  private List<SpireOgel> noEuOgels;

  private final String CONTROL_CODE_NAME = "controlCode";
  private final String CONTROL_CODE_PARAM = "CC01";
  private final String SOURCE_COUNTRY_NAME = "sourceCountry";
  private final String SOURCE_COUNTRY_PARAM = "1";
  private final String DESTINATION_COUNTRY_NAME = "destinationCountry";
  private final String DESTINATION_COUNTRY_PARAM = "2";

  @Before
  public void setUp() {
    this.euOgels = Arrays.asList(TestUtil.ogelEU(), TestUtil.ogelY(), TestUtil.ogelZ());
    this.noEuOgels = Arrays.asList(TestUtil.ogelX(), TestUtil.ogelY(), TestUtil.ogelZ());
  }

  @After
  public void tearDown(){
    reset(spireOgelService);
    reset(localOgelService);
  }

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new VirtualEuResource(spireOgelService, TestUtil.OGL61)).build();

  @Test
  public void controllerReturnsVirtualEuTrue() throws JSONException {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class))).thenReturn(euOgels);
    Response response = resources.client().target("/virtual-eu")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DESTINATION_COUNTRY_NAME, DESTINATION_COUNTRY_PARAM)
        .request().get();
    assertEquals(200, response.getStatus());
    JSONAssert.assertEquals("{\"ogelId\": \"" + TestUtil.OGL61 + "\", \"virtualEu\": true}", response.readEntity(String.class), true);
  }

  @Test
  public void controllerReturnsVirtualEuFalse() throws JSONException {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class))).thenReturn(noEuOgels);
    Response response = resources.client().target("/virtual-eu")
        .queryParam(CONTROL_CODE_NAME, CONTROL_CODE_PARAM)
        .queryParam(SOURCE_COUNTRY_NAME, SOURCE_COUNTRY_PARAM)
        .queryParam(DESTINATION_COUNTRY_NAME, DESTINATION_COUNTRY_PARAM)
        .request().get();
    assertEquals(200, response.getStatus());
    JSONAssert.assertEquals("{\"virtualEu\": false}", response.readEntity(String.class), true);
  }
}
