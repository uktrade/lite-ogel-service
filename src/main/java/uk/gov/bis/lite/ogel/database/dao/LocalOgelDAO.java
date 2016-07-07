package uk.gov.bis.lite.ogel.database.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.sql.SQLException;
import java.util.List;

public interface LocalOgelDAO {

  LocalOgel getOgelById(String ogelID);

  LocalOgel updateSingleOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws JsonProcessingException;

  LocalOgel insertLocalOgel(LocalOgel localOgel) throws JsonProcessingException;

  LocalOgel insertOrUpdate(LocalOgel ogel) throws JsonProcessingException;

  void insertLocalOgels(List<LocalOgel> ogelList) throws SQLException;

  List<LocalOgel> getAllOgels();
}
