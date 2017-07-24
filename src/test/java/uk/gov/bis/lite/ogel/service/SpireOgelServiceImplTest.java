package uk.gov.bis.lite.ogel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.spire.SpireOgelClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpireOgelServiceImplTest {

  private SpireOgelClient mockedOgelClient;
  private SpireOgelServiceImpl spireOgelService;

  private List<SpireOgel> initialOgels() {
    List<SpireOgel> ogels = new ArrayList<>();

    SpireOgel ogel1 = new SpireOgel();
    ogel1.setId("OGL1");
    ogels.add(ogel1);

    SpireOgel ogel2 = new SpireOgel();
    ogel2.setId("OGL2");
    ogels.add(ogel2);

    return ogels;
  }

  private List<SpireOgel> newOgels() {
    List<SpireOgel> ogels = new ArrayList<>();

    SpireOgel ogel1 = new SpireOgel();
    ogel1.setId("OGL3");
    ogels.add(ogel1);

    SpireOgel ogel2 = new SpireOgel();
    ogel2.setId("OGL4");
    ogels.add(ogel2);

    return ogels;
  }

  @Before
  public void setup() {
    //Create a SpireOgelServiceImpl with a mocked OGEL client which always returns two OGELs
    mockedOgelClient = mock(SpireOgelClient.class);
    when(mockedOgelClient.sendRequest(any())).thenReturn(initialOgels());

    spireOgelService = new SpireOgelServiceImpl(mockedOgelClient);
    //Populate the OGEL cache (in the real application this is done on startup)
    spireOgelService.refreshCache();
  }

  @Test
  public void testGetById() {
    Optional<SpireOgel> ogl1 = spireOgelService.findSpireOgelById("OGL1");
    assertThat(ogl1.get().getId()).isEqualTo("OGL1");
  }

  @Test
  public void testGetById_MissingOgel() {
    assertThat(spireOgelService.findSpireOgelById("MISSING_OGEL").isPresent()).isEqualTo(false);
  }

  @Test
  public void testGetAll() {
    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL1", "OGL2");
  }

  @Test
  public void testUninitialisedServiceThrowsException() {
    //Use a version of the service which does not populate any OGELs on startup
    SpireOgelService emptySpireOgelService = new SpireOgelServiceImpl(null);

    assertThatThrownBy(() -> emptySpireOgelService.getAllOgels()).isInstanceOf(CacheNotPopulatedException.class);
    assertThatThrownBy(() -> emptySpireOgelService.findSpireOgelById("OGL1")).isInstanceOf(CacheNotPopulatedException.class);
    assertThat(emptySpireOgelService.getHealthStatus().isHealthy()).isFalse();
    assertThat(emptySpireOgelService.getHealthStatus().getErrorMessage()).contains("Service not initialised");
  }

  @Test
  public void testRefreshCache() {
    //Return different OGELs to test the refresh behaviour
    when(mockedOgelClient.sendRequest(any())).thenReturn(newOgels());

    spireOgelService.refreshCache();

    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL3", "OGL4");
    assertThat(spireOgelService.getHealthStatus().isHealthy()).isTrue();
  }

  @Test
  public void testRefreshCache_NoOgels() {
    //Check we're in the expected state before starting
    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL1", "OGL2");

    //Simulate SPIRE error by returning 0 OGELs
    when(mockedOgelClient.sendRequest(any())).thenReturn(Collections.emptyList());

    spireOgelService.refreshCache();

    //Failed refresh should keep the old OGEL data
    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL1", "OGL2");
    //Healthcheck should report unhealthy due to 0 OGELs
    assertThat(spireOgelService.getHealthStatus().isHealthy()).isFalse();
    assertThat(spireOgelService.getHealthStatus().getErrorMessage()).contains("SPIRE returned 0 OGELs");
  }

  @Test
  public void testRefreshCache_ErrorThrown() {
    //Simulate SPIRE error by throwing an error
    when(mockedOgelClient.sendRequest(any())).thenThrow(new RuntimeException("Error getting data"));
    spireOgelService.refreshCache();

    //Failed refresh should keep the old OGEL data
    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL1", "OGL2");
    //Healthcheck should report unhealthy due to the error
    assertThat(spireOgelService.getHealthStatus().isHealthy()).isFalse();
    assertThat(spireOgelService.getHealthStatus().getErrorMessage()).contains("Error getting data");

    //Test that results and healthy status are restored when downstream service starts working again
    //(reuse the same mock object with a different stub result, to simulate the service coming back online)
    reset(mockedOgelClient);
    when(mockedOgelClient.sendRequest(any())).thenReturn(newOgels());
    spireOgelService.refreshCache();

    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL3", "OGL4");
    assertThat(spireOgelService.getHealthStatus().isHealthy()).isTrue();
  }

}