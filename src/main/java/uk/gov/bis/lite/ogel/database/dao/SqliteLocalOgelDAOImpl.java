package uk.gov.bis.lite.ogel.database.dao;

import static uk.gov.bis.lite.ogel.Main.DB_URL;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteLocalOgelDAOImpl implements LocalOgelDAO {

  @Override
  public List<LocalOgel> getAllLocalOgels() {
    List<LocalOgel> ogels = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(DB_URL);
         PreparedStatement selectOgelSt = connection.prepareStatement("SELECT ID, NAME FROM LOCAL_OGEL");
         PreparedStatement selectFromConditionSt = connection.prepareStatement("SELECT * FROM CONDITION_LIST WHERE OGELID=? AND TYPE=?");
         ResultSet rs = selectOgelSt.executeQuery()
    ) {
      LocalOgel ogel;
      while (rs.next()) {
        ogel = new LocalOgel();
        ogel.setId(rs.getString("ID"));
        ogel.setName(rs.getString("NAME"));

        OgelSummary summary = new OgelSummary();
        summary.setCanList(getConditions(selectFromConditionSt, ogel.getId(), "canList"));
        summary.setCantList(getConditions(selectFromConditionSt, ogel.getId(), "cantList"));
        summary.setMustList(getConditions(selectFromConditionSt, ogel.getId(), "mustList"));
        summary.setHowToUseList(getConditions(selectFromConditionSt, ogel.getId(), "howToUseList"));

        ogel.setSummary(summary);
        ogels.add(ogel);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ogels;
  }

  private List<String> getConditions(PreparedStatement st, String ogelId, String type) throws SQLException {
    st.setString(1, ogelId);
    st.setString(2, type);
    final ResultSet resultSet = st.executeQuery();
    List<String> conditionList = new ArrayList<>();
    while (resultSet.next()) {
      final String condition = resultSet.getString(3);
      conditionList.add(condition);
    }
    return conditionList;
  }

  @Override
  public LocalOgel getOgelById(String ogelID) {
    return getAllLocalOgels().stream().filter(o -> o.getId().equalsIgnoreCase(ogelID)).findFirst().
        orElseThrow(() -> new RuntimeException("Local Spire Could not be found with given id " + ogelID));
  }

  @Override
  public LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception {
    try (Connection connection = DriverManager.getConnection(DB_URL);
         PreparedStatement deletePS = connection.prepareStatement("DELETE FROM CONDITION_LIST WHERE OGELID = ? AND TYPE = ?");
         PreparedStatement insertPS = connection.prepareStatement("INSERT INTO CONDITION_LIST(OGELID, TYPE, CONDITION) VALUES (?, ?, ?)")) {
      connection.setAutoCommit(false);
      deletePS.setString(1, ogelID);
      deletePS.setString(2, fieldName);
      deletePS.executeUpdate();

      updateData.stream().forEach(u -> insertConditionListForOgel(insertPS, ogelID, fieldName, u));
      /*insertPS.setString(1, ogelID);
      insertPS.setString(2, fieldName);
      insertPS.setString(3, updateData.get(0));
      insertPS.executeUpdate();*/

      connection.commit();
    }
    return getOgelById(ogelID);
  }

  @Override
  public void insertLocalOgel(LocalOgel localOgel) {
    try (
        Connection connection = DriverManager.getConnection(DB_URL);

        PreparedStatement statement = connection.prepareStatement("INSERT INTO LOCAL_OGEL(ID, NAME) VALUES (?, ?)");
        PreparedStatement conditionListPS = connection.prepareStatement("INSERT INTO CONDITION_LIST(OGELID, TYPE, CONDITION) VALUES (?, ?, ?)");
    ) {
      statement.setString(1, localOgel.getId());
      statement.setString(2, localOgel.getName());
      statement.executeUpdate();
      localOgel.getSummary().getCanList().stream().forEach(condition -> {
        insertConditionListForOgel(conditionListPS, localOgel.getId(), "canList", condition);
      });
      localOgel.getSummary().getCantList().stream().forEach(condition -> {
        insertConditionListForOgel(conditionListPS, localOgel.getId(), "cantList", condition);
      });
      localOgel.getSummary().getMustList().stream().forEach(condition -> {
        insertConditionListForOgel(conditionListPS, localOgel.getId(), "mustList", condition);
      });
      localOgel.getSummary().getHowToUseList().stream().forEach(condition -> {
        insertConditionListForOgel(conditionListPS, localOgel.getId(), "howToUseList", condition);
      });
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createDatabase() {
    try (
        Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement localOgelTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS LOCAL_OGEL(ID TEXT, NAME TEXT)");
        PreparedStatement canListTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS CONDITION_LIST(OGELID TEXT, TYPE TEXT, CONDITION TEXT)")
    ) {
      localOgelTableStatement.execute();
      canListTableStatement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void insertConditionListForOgel(PreparedStatement ps, String id, String type, String condition) {
    try {
      ps.setString(1, id);
      ps.setString(2, type);
      ps.setString(3, condition);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
