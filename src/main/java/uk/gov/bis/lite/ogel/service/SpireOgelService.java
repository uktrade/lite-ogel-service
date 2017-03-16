package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.List;

public interface SpireOgelService {
  List<SpireOgel> getAllOgels();

  SpireOgel findSpireOgelById(String id) throws OgelNotFoundException;

  SpireHealthStatus getHealthStatus();
}
