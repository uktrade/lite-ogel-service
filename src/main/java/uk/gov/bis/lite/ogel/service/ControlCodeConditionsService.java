package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;

import java.util.Optional;

public interface ControlCodeConditionsService {
  Optional<ControlCodeConditionFullView> findControlCodeConditions(String ogelID, String controlCode);
}
