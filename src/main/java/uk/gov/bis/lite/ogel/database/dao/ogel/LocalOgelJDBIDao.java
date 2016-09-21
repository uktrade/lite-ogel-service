package uk.gov.bis.lite.ogel.database.dao.ogel;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public interface LocalOgelJDBIDao {

  @SqlQuery("SELECT ID, NAME, CANLIST, CANTLIST, MUSTLIST, HOWTOUSELIST FROM LOCAL_OGEL WHERE ID=:id")
  @Mapper(LocalOgelRSMapper.class)
  LocalOgel getLocalOgelById(@Bind("id") String ogelID);

  @SqlQuery("SELECT ID, NAME, CANLIST, CANTLIST, MUSTLIST, HOWTOUSELIST FROM LOCAL_OGEL")
  @Mapper(LocalOgelRSMapper.class)
  List<LocalOgel> getAllLocalOgels();


  @SqlUpdate("INSERT INTO LOCAL_OGEL (ID, NAME, CANLIST, CANTLIST, MUSTLIST, HOWTOUSELIST) " +
      "VALUES (:id, :name, :canList, :cantList, :mustList, :howToUseList)")
  void insertNewLocalOgel(@Bind("id") String id, @Bind("name") String name,
                          @Bind("canList") String canList, @Bind("cantList") String cantList,
                          @Bind("mustList") String mustList, @Bind("howToUseList") String howToUseList);
}
