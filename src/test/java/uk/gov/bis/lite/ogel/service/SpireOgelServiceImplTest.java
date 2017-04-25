package uk.gov.bis.lite.ogel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpireOgelServiceImplTest {

  private SpireOgelServiceImpl spireOgelService;

  @Before
  public void setup() {
    Map<String, SpireOgel> cache = new HashMap<>();

    SpireOgel ogel1 = new SpireOgel();
    ogel1.setId("OGL1");
    cache.put("OGL1", ogel1);

    SpireOgel ogel2 = new SpireOgel();
    ogel2.setId("OGL2");
    cache.put("OGL2", ogel2);

    spireOgelService = new SpireOgelServiceImpl(cache);
  }

  @Test
  public void testGetById() {
    SpireOgel ogl1 = spireOgelService.findSpireOgelById("OGL1");
    assertThat(ogl1.getId()).isEqualTo("OGL1");
  }

  @Test
  public void testGetById_MissingOgel() {
    assertThatThrownBy(() -> spireOgelService.findSpireOgelById("MISSING_OGEL")).isInstanceOf(OgelNotFoundException.class);
  }

  @Test
  public void testGetAll() {
    assertThat(spireOgelService.getAllOgels()).extracting(e -> e.getId()).containsOnly("OGL1", "OGL2");
  }

  @Test
  public void testEmptyCacheThrowsException() {
    spireOgelService = new SpireOgelServiceImpl(Collections.emptyMap());
    assertThatThrownBy(() -> spireOgelService.getAllOgels()).isInstanceOf(CacheNotPopulatedException.class);
    assertThatThrownBy(() -> spireOgelService.findSpireOgelById("OGL1")).isInstanceOf(CacheNotPopulatedException.class);
  }

  //TODO test for job?

}