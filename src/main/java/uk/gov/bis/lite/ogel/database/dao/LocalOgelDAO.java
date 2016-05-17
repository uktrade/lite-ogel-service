package uk.gov.bis.lite.ogel.database.dao;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public interface LocalOgelDAO {

  List<LocalOgel> getAllLocalOgels();

  LocalOgel getOgelById(String ogelID);

  LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName);
}
