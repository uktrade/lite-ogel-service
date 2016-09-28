package uk.gov.bis.lite.ogel.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.dropwizard.jersey.errors.ErrorMessage;
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
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.SpireOgelTestUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class ApplicableOgelResourceTest {

  private static final SpireOgelService spireOgelService = Mockito.mock(SpireOgelService.class);
  private static final LocalOgelService localOgelService = Mockito.mock(LocalOgelService.class);
  List<SpireOgel> spireOgels;

  @Before
  public void setUp() {
    Country firstBannedCountry = new Country("id3", "AF", "Afghanistan");
    Country secondBannedCountry = new Country("id4", "SY", "Syria");
    Country thirdBannedCountry = new Country("id5", "NZ", "New Zealand");
    List<Country> bannedCountries = Arrays.asList(firstBannedCountry, secondBannedCountry, thirdBannedCountry);

    List<Rating> ratings = new ArrayList<>();
    ratings.add(SpireOgelTestUtility.createRating("ML21a", true));
    ratings.add(SpireOgelTestUtility.createRating("ML21b1", true));
    ratings.add(SpireOgelTestUtility.createRating("ML21b2", false));

    OgelCondition ogelCondition = new OgelCondition();
    ogelCondition.setExcludedCountries(bannedCountries);
    ogelCondition.setRatingList(ratings);
    List<OgelCondition> conditionsList = Collections.singletonList(ogelCondition);
    SpireOgel firstOgel = SpireOgelTestUtility.createOgel("OGL0", "description", conditionsList, ActivityType.TECH);
    spireOgels = Collections.singletonList(firstOgel);

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
  public void controllerReturnsExpectedOgelList() {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class))).thenReturn(spireOgels);
    final Response response = resources.client().target("/applicable-ogels").queryParam("controlCode", "ML1a")
        .queryParam("sourceCountry", "41")
        .queryParam("destinationCountry", "1")
        .queryParam("destinationCountry", "2")
        .queryParam("activityType", "TECH").request().get();

    final List<SpireOgel> spireOgelsResponse = (List<SpireOgel>) response.readEntity(List.class);
    assertEquals(this.spireOgels.size(), spireOgelsResponse.size());
    assertEquals(this.spireOgels.get(0).getName(), ((Map) spireOgelsResponse.get(0)).get("name"));
    assertEquals(this.spireOgels.get(0).getId(), ((Map) spireOgelsResponse.get(0)).get("id"));

  }

  @Test
  public void returnsInternalServerErrorWhenListEmpty() {
    String errorMessage = "Spire Ogel List Empty";
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class)))
        .thenThrow(new RuntimeException(errorMessage));
    final Response response = resources.client().target("/applicable-ogels").queryParam("controlCode", "ML1a")
        .queryParam("sourceCountry", "41").queryParam("destinationCountry", "1")
        .queryParam("activityType", "TECH").request().get();
    assertEquals(500, response.getStatus());
    assertEquals(errorMessage, response.readEntity(ErrorMessage.class).getMessage());
  }

  @Test
  public void throwsWebApplicationExceptionForInvalidCategory() throws IOException {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class))).thenCallRealMethod();
    final Response response = resources.client().target("/applicable-ogels").queryParam("controlCode", "ML1a")
        .queryParam("sourceCountry", "41").queryParam("destinationCountry", "1")
        .queryParam("activityType", "Invalid").request().get();
    assertNotNull(response);
    assertEquals(response.getStatus(), 400); //Bad Request
  }

  @Test
  public void returnsEmptyListWhenNoOgelsMatched() {
    when(spireOgelService.findOgel(anyString(), Arrays.asList(anyString()), anyListOf(ActivityType.class))).thenReturn(Collections.emptyList());
    final Response response = resources.client()
        .target("/applicable-ogels")
        .queryParam("controlCode", "ML1a")
        .queryParam("sourceCountry", "41")
        .queryParam("destinationCountry", "1")
        .queryParam("activityType", "TECH")
        .request().get();
    final List<SpireOgel> spireOgelsResponse = (List<SpireOgel>) response.readEntity(List.class);
    assertEquals(200, response.getStatus());
    assertEquals(0, spireOgelsResponse.size());
  }

}
