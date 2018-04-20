package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.local.ogel.LocalControlCodeCondition;

import java.util.List;

import javax.annotation.Nullable;

public interface LocalControlCodeConditionService {

  @Nullable
  LocalControlCodeCondition getLocalControlCodeConditionsByIdAndControlCode(String ogelID, String controlCode);

  void insertControlCodeConditionList(List<LocalControlCodeCondition> controlCodeConditionList);

  List<LocalControlCodeCondition> getAllControlCodeConditions();

  void deleteControlCodeConditions();
}
