package uk.gov.bis.lite.ogel.database.dao;

import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.sql.SQLException;
import java.util.List;

public interface LocalOgelDAO {

  LocalOgel getOgelById(String ogelID) throws LocalOgelNotFoundException;

  LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception;

  LocalOgel insertLocalOgel(LocalOgel localOgel);

  void deleteOgel(String ogelID);

  LocalOgel insertOrUpdate(LocalOgel ogel);

  void insertLocalOgels(List<LocalOgel> ogelList) throws SQLException;
}
