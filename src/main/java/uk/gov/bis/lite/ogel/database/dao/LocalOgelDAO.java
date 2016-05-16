package uk.gov.bis.lite.ogel.database.dao;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public interface LocalOgelDAO {

  List<LocalOgel> getAllLocalSpireOgels();

  LocalOgel getSpireOgelById(String ogelID);

  LocalOgel updateSpireOgelCanList(String ogelID, List<String> updateData, String fieldName);
}
