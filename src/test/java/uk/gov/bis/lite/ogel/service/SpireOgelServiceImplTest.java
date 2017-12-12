package uk.gov.bis.lite.ogel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpireOgelServiceImplTest {

  private SpireOgelServiceImpl spireOgelService;

  @Mock
  private SpireOgelCache spireOgelCache;

  private Map<String, SpireOgel> initialSpireCacheOgels() {
    List<SpireOgel> ogels = new ArrayList<>();

    SpireOgel ogel1 = new SpireOgel();
    ogel1.setId("OGL1");
    ogels.add(ogel1);

    SpireOgel ogel2 = new SpireOgel();
    ogel2.setId("OGL2");
    ogels.add(ogel2);

    return ogels.stream().collect(Collectors.toMap(SpireOgel::getId, e -> e));
  }

  @Before
  public void setup() {
    //spireOgelCache always returns two OGELs
    spireOgelCache = mock(SpireOgelCache.class);

    spireOgelService = new SpireOgelServiceImpl(spireOgelCache);
    when(spireOgelCache.getCache()).thenReturn(initialSpireCacheOgels());
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
    assertThat(spireOgelService.getAllOgels()).extracting(SpireOgel::getId).containsOnly("OGL1", "OGL2");
  }

  @Test
  public void testUninitialisedServiceThrowsException() {
    when(spireOgelCache.getCache()).thenReturn(new HashMap<>());
    when(spireOgelCache.getHealthStatus()).thenReturn(SpireHealthStatus.unhealthy("Cache not initialised"));
    //empty cache
    SpireOgelService emptySpireOgelService = new SpireOgelServiceImpl(spireOgelCache);

    assertThatThrownBy(emptySpireOgelService::getAllOgels).isInstanceOf(CacheNotPopulatedException.class);
    assertThatThrownBy(() -> emptySpireOgelService.findSpireOgelById("OGL1")).isInstanceOf(CacheNotPopulatedException.class);
    assertThat(emptySpireOgelService.getHealthStatus().isHealthy()).isFalse();
    assertThat(emptySpireOgelService.getHealthStatus().getErrorMessage()).contains("Cache not initialised");
  }
}