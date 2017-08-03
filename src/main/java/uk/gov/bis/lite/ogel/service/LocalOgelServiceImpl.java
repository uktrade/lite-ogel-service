package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.database.dao.ogel.LocalOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;
import java.util.Optional;

@Singleton
public class LocalOgelServiceImpl implements LocalOgelService {

  @Inject
  private LocalOgelDAO localOgelDAO;

  @Override
  public LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) {
    return localOgelDAO.updateSingleOgelConditionList(ogelID, newConditionList, conditionField);
  }

  @Override
  public Optional<LocalOgel> findLocalOgelById(String id) {
    LocalOgel localOgel = localOgelDAO.getOgelById(id);
    return Optional.ofNullable(localOgel);
  }

  @Override
  public LocalOgel insertOrUpdateOgel(LocalOgel ogel) {
    return localOgelDAO.insertOrUpdate(ogel);
  }

  @Override
  public void insertOgelList(List<LocalOgel> ogelList) {
    for(LocalOgel lo : ogelList){
      localOgelDAO.insertOrUpdate(lo);
    }
  }

  @Override
  public List<LocalOgel> getAllLocalOgels() {
    return localOgelDAO.getAllOgels();
  }

  @Override
  public void deleteAllOgels() {
    localOgelDAO.deleteAllOgels();
  }

  @Override
  public void deleteOgelById(String ogelId) {
    localOgelDAO.deleteOgelById(ogelId);
  }

}
