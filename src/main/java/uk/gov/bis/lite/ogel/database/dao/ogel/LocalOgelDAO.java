package uk.gov.bis.lite.ogel.database.dao.ogel;

import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;

import java.util.List;

public interface LocalOgelDAO {

  LocalOgel getOgelById(String ogelID);

  LocalOgel updateSingleOgelConditionList(String ogelID, List<String> updateData, String fieldName);

  LocalOgel insertLocalOgel(LocalOgel localOgel);

  LocalOgel insertOrUpdate(LocalOgel ogel);

  List<LocalOgel> getAllOgels();

  void deleteAllOgels();

  void deleteOgelById(String ogelId);

}
