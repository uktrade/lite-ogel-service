package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class SpireOgelServiceImpl implements SpireOgelService {

  private final SpireOgelCache spireOgelCache;

  @Inject
  public SpireOgelServiceImpl(SpireOgelCache spireOgelCache) {
    this.spireOgelCache = spireOgelCache;
  }

  @Override
  public List<SpireOgel> getAllOgels() {
    if (spireOgelCache.getCache().isEmpty()) {
      throw new CacheNotPopulatedException("Communication with Spire failed. Spire Ogel list is not populated");
    }
    return new ArrayList<>(spireOgelCache.getCache().values());
  }

  @Override
  public Optional<SpireOgel> findSpireOgelById(String id) {
    if (spireOgelCache.getCache().isEmpty()) {
      throw new CacheNotPopulatedException("Communication with Spire failed. Spire Ogel list is not populated");
    }
    return Optional.ofNullable(spireOgelCache.getCache().get(id));
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return spireOgelCache.getHealthStatus();
  }
}
