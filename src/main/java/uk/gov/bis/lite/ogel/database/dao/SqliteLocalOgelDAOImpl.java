package uk.gov.bis.lite.ogel.database.dao;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import uk.gov.bis.lite.ogel.exception.OgelIDNotFoundException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqliteLocalOgelDAOImpl implements LocalOgelDAO {
  private final DBI jdbi;

  @Inject
  public SqliteLocalOgelDAOImpl(@Named("jdbi") DBI jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<LocalOgel> getAllOgels() {
    try (final Handle handle = jdbi.open()) {
      List<LocalOgel> list = handle.createQuery("SELECT ID, NAME FROM LOCAL_OGEL ORDER BY ROWID")
          .map(LocalOgel.class).list();
      list.forEach(lo -> lo.setSummary(getConditionList(handle, lo.getId())));
      return list;
    }
  }

  @Override
  public LocalOgel getOgelById(String ogelID) {
    LocalOgel ogel = new LocalOgel();
    try (final Handle handle = jdbi.open()) {
      final Map<String, Object> selectQuery = handle.createQuery("SELECT ID, NAME FROM LOCAL_OGEL WHERE ID=:id").bind("id", ogelID).first();
      if (selectQuery == null) {
        return null;
      }
      ogel.setId(selectQuery.get("id").toString());
      //null check for when checking if localOgel exists with just ID
      if (selectQuery.get("name") != null) {
        ogel.setName(selectQuery.get("name").toString());
      }
      OgelConditionSummary summary = getConditionList(handle, ogelID);
      ogel.setSummary(summary);
      return ogel;
    }
  }

  @Override
  public LocalOgel updateSingleOgelConditionList(String ogelID, List<String> updateData, String fieldName) {
    try (final Handle handle = jdbi.open()) {
      handle.execute("DELETE FROM CONDITION_LIST WHERE OGELID = ? AND TYPE = ?", ogelID, fieldName);
      updateData.forEach(u -> insertConditionListForOgel(handle, ogelID, fieldName, u));
    }
    return getOgelById(ogelID);
  }

  @Override
  public LocalOgel insertLocalOgel(LocalOgel localOgel) {
    try (final Handle handle = jdbi.open()) {
      transactionalInsertOgel(handle, localOgel);
    }
    return localOgel;
  }

  private LocalOgel transactionalInsertOgel(Handle handle, LocalOgel localOgel) throws OgelIDNotFoundException {
    if (localOgel.getName() != null) {
      handle.execute("INSERT INTO LOCAL_OGEL(ID, NAME) VALUES (?, ?)", localOgel.getId(), localOgel.getName());
    } else { //insert only id no name
      handle.execute("INSERT INTO LOCAL_OGEL(ID) VALUES (?)", localOgel.getId());
    }
    if (localOgel.getSummary() != null) {
      localOgel.getSummary().getCanList().forEach(condition -> insertConditionListForOgel(handle, localOgel.getId(), "canList", condition));
      localOgel.getSummary().getCantList().forEach(condition -> insertConditionListForOgel(handle, localOgel.getId(), "cantList", condition));
      localOgel.getSummary().getMustList().forEach(condition -> insertConditionListForOgel(handle, localOgel.getId(), "mustList", condition));
      localOgel.getSummary().getHowToUseList().forEach(condition -> insertConditionListForOgel(handle, localOgel.getId(), "howToUseList", condition));
    }
    return localOgel;
  }

  @Override
  public void deleteOgel(String ogelID) {
    try (final Handle handle = jdbi.open()) {
      transactionalDeleteLocalOgel(handle, ogelID);
    }
  }

  @Override
  public LocalOgel insertOrUpdate(LocalOgel newOgel) {
    LocalOgel ogelFoundById = getOgelById(newOgel.getId());
    if (ogelFoundById == null) {
      return insertLocalOgel(newOgel);
    }
    try (final Handle handle = jdbi.open()) {
      if (newOgel.getName() != null) {
        handle.createStatement("UPDATE LOCAL_OGEL SET NAME=:name WHERE ID:=id")
            .bind("name", newOgel.getName()).bind("id", newOgel.getId()).execute();
      }
      if (newOgel.getSummary() != null) {
        updateLocalOgelConditionsList(handle, newOgel);
      }
    }
    return newOgel;
  }

  @Override
  public void insertLocalOgels(List<LocalOgel> ogelList) throws SQLException {
    try (final Handle handle = jdbi.open()) {
      handle.getConnection().setAutoCommit(false);
      handle.begin();
      ogelList.forEach(o -> transactionalDeleteLocalOgel(handle, o.getId()));
      for (LocalOgel lo : ogelList) {
        transactionalInsertOgel(handle, lo);
      }
      handle.commit();
      handle.close();
    } catch (UnableToExecuteStatementException e) {
      throw new SQLException(e.getCause());
    }
  }

  private void transactionalDeleteLocalOgel(Handle handle, String ogelID) {
    handle.execute("DELETE FROM LOCAL_OGEL WHERE ID = ?", ogelID);
    handle.execute("DELETE FROM CONDITION_LIST WHERE OGELID = ? ", ogelID);
  }

  private void insertConditionListForOgel(Handle handle, String id, String type, String condition) {
    handle.execute("INSERT INTO CONDITION_LIST(OGELID, TYPE, CONDITION) VALUES (?, ?, ?)", id, type, condition);
  }

  private void updateLocalOgelConditionsList(Handle handle, LocalOgel newOgel) {
    String id = newOgel.getId();
    handle.createStatement("DELETE FROM CONDITION_LIST WHERE OGELID=:id").bind("id", id).execute();
    newOgel.getSummary().getCanList().forEach(cond -> insertConditionListForOgel(handle, id, "canList", cond));
    newOgel.getSummary().getCantList().forEach(cond -> insertConditionListForOgel(handle, id, "cantList", cond));
    newOgel.getSummary().getMustList().forEach(cond -> insertConditionListForOgel(handle, id, "mustList", cond));
    newOgel.getSummary().getHowToUseList().forEach(cond -> insertConditionListForOgel(handle, id, "howToUseList", cond));
  }

  private OgelConditionSummary getConditionList(Handle handler, String ogelID) {
    final List<Map<String, Object>> list = handler.createQuery("SELECT * FROM CONDITION_LIST WHERE OGELID=:id ORDER BY ROWID")
        .bind("id", ogelID).list();
    OgelConditionSummary summary = new OgelConditionSummary();
    summary.setCanList(getSpecificConditionList("canList", list));
    summary.setCantList(getSpecificConditionList("cantList", list));
    summary.setMustList(getSpecificConditionList("mustList", list));
    summary.setHowToUseList(getSpecificConditionList("howToUseList", list));
    return summary;
  }

  private List<String> getSpecificConditionList(String conditionType, List<Map<String, Object>> list) {
    return list.stream().filter(c -> c.get("type").toString().equalsIgnoreCase(conditionType))
        .map(cond -> cond.get("condition").toString()).collect(Collectors.toList());
  }
}
