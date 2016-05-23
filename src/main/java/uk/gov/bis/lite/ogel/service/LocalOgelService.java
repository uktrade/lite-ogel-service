package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;
import java.util.Optional;

@Singleton
public class LocalOgelService {

  @Inject
  private LocalOgelDAO localOgelDAO;

  public List<?> getAllLocalOgels() {
    return localOgelDAO.getAllLocalOgels();
  }

  public LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) throws Exception {
    return localOgelDAO.updateOgelConditionList(ogelID, newConditionList, conditionField);
  }

  public Optional<LocalOgel> findLocalOgelById(List<LocalOgel> ogelList, String id) {
    return ogelList.stream().filter(lo -> id.equalsIgnoreCase(lo.getId())).findAny();
  }
}