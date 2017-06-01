package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.List;

public interface LocalControlCodeConditionService {
  LocalControlCodeCondition getLocalControlCodeConditionsByIdAndControlCode(String ogelID, String controlCode);

  void insertControlCodeConditionList(List<LocalControlCodeCondition> controlCodeConditionList);

  List<LocalControlCodeCondition> getAllControlCodeConditions();

  void deleteControlCodeConditions();
}
