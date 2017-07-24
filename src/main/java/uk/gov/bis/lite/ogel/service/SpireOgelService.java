package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.List;
import java.util.Optional;

public interface SpireOgelService {
  List<SpireOgel> getAllOgels();

  Optional<SpireOgel> findSpireOgelById(String id);

  SpireHealthStatus getHealthStatus();
}
