package uk.gov.bis.lite.ogel.database.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SqliteLocalOgelDAOImpl implements LocalOgelDAO {
  private final DBI jdbi;

  @Inject
  public SqliteLocalOgelDAOImpl(@Named("jdbi") DBI jdbi) {
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
  public LocalOgel updateSingleOgelConditionList(String ogelID, List<String> updateData, String fieldName)
      throws JsonProcessingException {
    try (final Handle handle = jdbi.open()) {
      updateOgelCondition(handle, ogelID, fieldName, parseListToJson(updateData));
    }
    return getOgelById(ogelID);
  }

  @Override
  public LocalOgel insertLocalOgel(LocalOgel localOgel) throws JsonProcessingException {
    try (final Handle handle = jdbi.open()) {
      insertLocalOgel(localOgel, handle);
      return getOgelById(localOgel.getId());
    }
  }

  private void insertLocalOgel(LocalOgel localOgel, Handle handle) throws JsonProcessingException {
    LocalOgelJDBIDao jdbiDao = handle.attach(LocalOgelJDBIDao.class);
    jdbiDao.insertNewLocalOgel(localOgel.getId(), localOgel.getName(), parseListToJson(localOgel.getSummary().getCanList()),
        parseListToJson(localOgel.getSummary().getCantList()), parseListToJson(localOgel.getSummary().getMustList()),
        parseListToJson(localOgel.getSummary().getHowToUseList()));
  }

  private String parseListToJson(List<String> conditionList) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(conditionList);
  }

  @Override
  public LocalOgel insertOrUpdate(LocalOgel newOgel) throws JsonProcessingException {
    LocalOgel ogelFoundById = getOgelById(newOgel.getId());
    if (ogelFoundById == null) {
      return insertLocalOgel(newOgel);
    }
    return updateLocalOgel(newOgel);
  }

  @Override
  public void insertLocalOgels(List<LocalOgel> ogelList) throws SQLException {
    try (final Handle handle = jdbi.open()) {
      handle.getConnection().setAutoCommit(false);
      handle.begin();
      for (LocalOgel lo : ogelList) {
        insertLocalOgel(lo, handle);
      }
      handle.commit();
      handle.close();
    } catch (UnableToExecuteStatementException e) {
      throw new SQLException(e.getCause());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private LocalOgel updateLocalOgel(LocalOgel ogel) throws JsonProcessingException {
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
