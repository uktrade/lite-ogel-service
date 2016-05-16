package uk.gov.bis.lite.ogel.database.dao;

import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;

import java.util.List;

public interface LocalSpireOgelDAO {

  List<LocalSpireOgel> getAllLocalSpireOgels();

  LocalSpireOgel getSpireOgelById(String ogelID);

  LocalSpireOgel updateSpireOgelCanList(String ogelID, List<String> updateData, String fieldName);
}
