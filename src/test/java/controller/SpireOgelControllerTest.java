package controller;

import com.bis.lite.ogel.controller.SpireOgelController;
import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.OgelCondition;
import com.bis.lite.ogel.model.Rating;
import com.bis.lite.ogel.model.SpireOgel;
import com.bis.lite.ogel.service.SpireOgelService;
import com.bis.lite.ogel.util.SpireOgelTestUtility;

import net.sf.ehcache.CacheManager;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.ResourceTestRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpireOgelControllerTest {

    private static final SpireOgelService service = Mockito.mock(SpireOgelService.class);
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
        List<OgelCondition> conditionsList = Arrays.asList(ogelCondition);
        SpireOgel firstOgel = SpireOgelTestUtility.createOgel("OGL0", "description", conditionsList, CategoryType.TECH);
        spireOgels = Arrays.asList(firstOgel);

    }

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new SpireOgelController(service))
            .build();

    @Test
    public void controllerReturnsExpectedOgelList() {
        when(service.findOgel(anyString(), anyString(), anyListOf(CategoryType.class))).thenReturn(spireOgels);
        final Response response = resources.client().target("/applicable-ogels").queryParam("controlCode", "ML1a")
                .queryParam("sourceCountry", "41").queryParam("destinationCountry", "1")
                .queryParam("activityType", "TECH").request().get();

        final List<SpireOgel> spireOgelsResponse = (List<SpireOgel>) response.readEntity(List.class);
        assertEquals(this.spireOgels.size(), spireOgelsResponse.size());
        assertEquals(this.spireOgels.get(0).getDescription(), ((Map) spireOgelsResponse.get(0)).get("description"));
        assertEquals(this.spireOgels.get(0).getId(), ((Map) spireOgelsResponse.get(0)).get("id"));

    }

    @Ignore
    @Test
    public void throwsWebApplicationExceptionForInvalidCategory() throws IOException {
        when(service.findOgel(anyString(), anyString(), anyListOf(CategoryType.class))).thenCallRealMethod();
        final Response response = resources.client().target("/applicable-ogels").queryParam("controlCode", "ML1a")
                .queryParam("sourceCountry", "41").queryParam("destinationCountry", "1")
                .queryParam("activityType", "Invalid").request().get();
        assertNotNull(response);
        assertEquals(response.getStatus(), 400); //Bad Request
    }
}
