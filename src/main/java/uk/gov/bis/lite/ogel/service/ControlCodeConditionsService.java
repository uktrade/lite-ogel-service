package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;

import javax.annotation.Nullable;

public interface ControlCodeConditionsService {
  @Nullable
  ControlCodeConditionFullView findControlCodeConditions(String ogelID, String controlCode);
}
