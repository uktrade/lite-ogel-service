package uk.gov.bis.lite.ogel.database.dao;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.sql.SQLException;
import java.util.List;

public interface LocalOgelDAO {

  LocalOgel getOgelById(String ogelID);

  LocalOgel updateSingleOgelConditionList(String ogelID, List<String> updateData, String fieldName);

  LocalOgel insertLocalOgel(LocalOgel localOgel) throws SQLException;

  void deleteOgel(String ogelID);

  LocalOgel insertOrUpdate(LocalOgel ogel) throws SQLException;

  void insertLocalOgels(List<LocalOgel> ogelList) throws SQLException;

  List<LocalOgel> getAllOgels();
}
