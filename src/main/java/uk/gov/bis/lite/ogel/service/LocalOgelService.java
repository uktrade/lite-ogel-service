package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public interface LocalOgelService {
  LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField);

  LocalOgel findLocalOgelById(String id);

  LocalOgel insertOrUpdateOgel(LocalOgel ogel);

  void insertOgelList(List<LocalOgel> ogelList);

  List<LocalOgel> getAllLocalOgels();

  void deleteAllOgels();

  void deleteOgelById(String ogelId);
}
