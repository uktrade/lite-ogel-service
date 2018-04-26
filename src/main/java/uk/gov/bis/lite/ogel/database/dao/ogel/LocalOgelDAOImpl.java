package uk.gov.bis.lite.ogel.database.dao.ogel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class LocalOgelDAOImpl implements LocalOgelDAO {
  private final DBI jdbi;

  @Inject
  public LocalOgelDAOImpl(@Named("jdbi") DBI jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<LocalOgel> getAllOgels() {
    try (final Handle handle = jdbi.open()) {
      LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
      return jdbiDao.getAllLocalOgels();
    }
  }

  @Override
  public LocalOgel getOgelById(String ogelID) {
    try (final Handle handle = jdbi.open()) {
      LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
      return jdbiDao.getLocalOgelById(ogelID);
    }
  }

  @Override
  public LocalOgel updateSingleOgelConditionList(String ogelID, List<String> updateData, String fieldName) {
    try (final Handle handle = jdbi.open()) {
      if (getOgelById(ogelID) == null) {
        LocalOgel newOgel = new LocalOgel();
        newOgel.setId(ogelID);
        LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
        jdbiDao.insertNewLocalOgel(newOgel.getId(), null, null, null, null, null);
      }
      updateOgelCondition(handle, ogelID, fieldName, parseListToJson(updateData));
    }
    return getOgelById(ogelID);
  }

  @Override
  public LocalOgel insertLocalOgel(LocalOgel localOgel) {
    try (final Handle handle = jdbi.open()) {
      insertLocalOgel(localOgel, handle);
      return getOgelById(localOgel.getId());
    }
  }

  private void insertLocalOgel(LocalOgel localOgel, Handle handle) {
    LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
    if (localOgel.getName() == null) {
      jdbiDao.insertNewLocalOgel(localOgel.getId(), null, parseListToJson(localOgel.getSummary().getCanList()),
          parseListToJson(localOgel.getSummary().getCantList()), parseListToJson(localOgel.getSummary().getMustList()),
          parseListToJson(localOgel.getSummary().getHowToUseList()));
    } else if (localOgel.getSummary() == null) {
      jdbiDao.insertNewLocalOgel(localOgel.getId(), localOgel.getName(), null, null, null, null);
    } else {
      jdbiDao.insertNewLocalOgel(localOgel.getId(), localOgel.getName(), parseListToJson(localOgel.getSummary().getCanList()),
          parseListToJson(localOgel.getSummary().getCantList()), parseListToJson(localOgel.getSummary().getMustList()),
          parseListToJson(localOgel.getSummary().getHowToUseList()));
    }
  }

  private String parseListToJson(List<String> conditionList) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(conditionList);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public LocalOgel insertOrUpdate(LocalOgel newOgel) {
    LocalOgel ogelFoundById = getOgelById(newOgel.getId());
    if (ogelFoundById == null) {
      return insertLocalOgel(newOgel);
    }
    return updateLocalOgel(newOgel);
  }

  @Override
  public void deleteAllOgels() {
    try (final Handle handle = jdbi.open()) {
      LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
      jdbiDao.deleteAllOgels();
    }
  }

  @Override
  public void deleteOgelById(String ogelId) {
    try (final Handle handle = jdbi.open()) {
      LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
      jdbiDao.deleteOgelById(ogelId);
    }
  }

  private LocalOgel updateLocalOgel(LocalOgel ogel) {
    StringJoiner sj = new StringJoiner(" ,");
    Map<String, Object> bindMappings = new HashMap<>();
    bindMappings.put("id", ogel.getId());

    if (ogel.getName() != null) {
      sj.add("NAME = :name");
      bindMappings.put("name", ogel.getName());
    }
    if (ogel.getSummary() != null) {
      sj.add("CANLIST = :canList");
      bindMappings.put("canList", parseListToJson(ogel.getSummary().getCanList()));
      sj.add("CANTLIST = :cantList");
      bindMappings.put("cantList", parseListToJson(ogel.getSummary().getCantList()));
      sj.add("MUSTLIST = :mustList");
      bindMappings.put("mustList", parseListToJson(ogel.getSummary().getMustList()));
      sj.add("HOWTOUSELIST = :howToUseList");
      bindMappings.put("howToUseList", parseListToJson(ogel.getSummary().getHowToUseList()));
    }
    StringBuilder updateQuery = new StringBuilder();
    updateQuery.append("UPDATE LOCAL_OGEL SET ");
    updateQuery.append(sj.toString());
    updateQuery.append(" WHERE ID = :id");
    try (final Handle handle = jdbi.open()) {
      Update update = handle.createStatement(updateQuery.toString());
      update.bindFromMap(bindMappings).execute();
      return getOgelById(ogel.getId());
    }
  }

  private void updateOgelCondition(Handle handle, String id, String type, String condition) {
    handle.createStatement("UPDATE LOCAL_OGEL SET " + type + " = :condition " + " WHERE ID = :id")
        .bind("condition", condition).bind("id", id).execute();
  }
}
