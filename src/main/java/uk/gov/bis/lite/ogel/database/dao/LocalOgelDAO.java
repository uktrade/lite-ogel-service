package uk.gov.bis.lite.ogel.database.dao;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public interface LocalOgelDAO {

  LocalOgel getOgelById(String ogelID);

  LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception;

  void insertLocalOgel(LocalOgel localOgel);
}
