package uk.gov.bis.lite.ogel.resource;

import static org.junit.Assert.assertEquals;
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
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.SpireOgelTestUtility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class VirtualEuResourceTest {

  private static final SpireOgelService spireOgelService = Mockito.mock(SpireOgelService.class);
  private static final LocalOgelService localOgelService = Mockito.mock(LocalOgelService.class);
  private List<SpireOgel> virtualEuOgels;
  private List<SpireOgel> nonVirtualEuOgels;

  private final String VIRTUAL_EU_TRUE = "{\"virtualEu\": true}";
  private final String VIRTUAL_EU_FALSE = "{\"virtualEu\": false}";

  /**/
  @Before
  public void setUp() {
    OgelCondition condition = new OgelCondition();
    condition.setIncludedCountries(Arrays.asList(new Country("id3", "AF", "France")));
    condition.setRatingList(Arrays.asList(SpireOgelTestUtility.createRating("ML21a", true)));
    List<OgelCondition> conditions = Collections.singletonList(condition);
    virtualEuOgels = Collections.singletonList(SpireOgelTestUtility.createOgel("OGL61", "desc", conditions, ActivityType.DU_ANY));
    nonVirtualEuOgels = Collections.singletonList(SpireOgelTestUtility.createOgel("OGXXX", "desc", conditions, ActivityType.DU_ANY));
  }

  @After
  public void tearDown(){
    reset(spireOgelService);
    reset(localOgelService);
  }

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new VirtualEuResource(spireOgelService, "OGL61"))
      .build();

  @Test
  public void controllerReturnsVirtualEuTrue() {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class)))
        .thenReturn(virtualEuOgels);
    final Response response = resources.client().target("/virtual-eu").queryParam("controlCode", "ML1a")
        .queryParam("sourceCountry", "41")
        .queryParam("destinationCountry", "1").request().get();
    assertEquals(200, response.getStatus());
    assertEquals(response.readEntity(String.class), VIRTUAL_EU_TRUE);
  }

  @Test
  public void controllerReturnsVirtualEuFalse() {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class)))
        .thenReturn(nonVirtualEuOgels);
    final Response response = resources.client().target("/virtual-eu").queryParam("controlCode", "ML1a")
        .queryParam("sourceCountry", "41")
        .queryParam("destinationCountry", "1").request().get();
    assertEquals(200, response.getStatus());
    assertEquals(response.readEntity(String.class), VIRTUAL_EU_FALSE);
  }
}
