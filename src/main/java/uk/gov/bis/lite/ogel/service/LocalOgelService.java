package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.validator.CheckLocalOgel;

import java.util.List;

public interface LocalOgelService {
  LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField);

  LocalOgel findLocalOgelById(String id);

  LocalOgel insertOrUpdateOgel(@CheckLocalOgel LocalOgel ogel);

  void insertOgelList(List<LocalOgel> ogelList);

  List<LocalOgel> getAllLocalOgels();

  void deleteAllOgels();

  void deleteOgelById(String ogelId);
}
