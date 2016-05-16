package uk.gov.bis.lite.ogel.database;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.database.dao.LocalSpireOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgelLookUp;
import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;

import java.io.IOException;
import java.util.List;

@Singleton
public class LocalSpireOgelFlatFileImpl implements LocalSpireOgelDAO {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalSpireOgelFlatFileImpl.class);
  private static final String LOCAL_OGEL_CONDITION_DATA_FILE = "ogel-condition-data.json";
  private static final String LOCAL_OGEL_LOOKUP_DATA_FILE = "ogel-lookup-data.json";

  private static List<LocalSpireOgel> localOgels;

  @Override
  public List<LocalSpireOgel> getAllLocalSpireOgels() {
    if (localOgels == null) {
      try {
        LOGGER.info("Storing the values retrieved from {}", LOCAL_OGEL_CONDITION_DATA_FILE);
        List<LocalSpireOgel> localSpireOgelList = (List<LocalSpireOgel>)
            new ListJsonMapper().mapToListFromJson(LOCAL_OGEL_CONDITION_DATA_FILE, LocalSpireOgel.class);

        LOGGER.info("Storing the values retrieved from {}", LOCAL_OGEL_LOOKUP_DATA_FILE);
        List<LocalOgelLookUp> localOgelLookUpList = (List<LocalOgelLookUp>)
            new ListJsonMapper().mapToListFromJson(LOCAL_OGEL_LOOKUP_DATA_FILE, LocalOgelLookUp.class);

        for (LocalSpireOgel localOgel : localSpireOgelList) {
          localOgelLookUpList.stream().filter(localLookUp -> localOgel.getName().equalsIgnoreCase(localLookUp.getName()))
              .forEach(localLookUp -> {
                localOgel.setId(localLookUp.getId());
              });
        }
        localOgels = localSpireOgelList;
        return localSpireOgelList;
      } catch (IOException e) {
        LOGGER.warn("An error occurred trying to populate the database", e);
      }
    }
    return localOgels;
  }

  @Override
  public LocalSpireOgel getSpireOgelById(String ogelID) {
    if (localOgels == null) {
      getAllLocalSpireOgels();
    }
    return localOgels.stream().filter(o -> o.getId().equalsIgnoreCase(ogelID)).findAny().get();
  }

  @Override
  public LocalSpireOgel updateSpireOgelCanList(String ogelID, List<String> updateData, String fieldName) {
    final LocalSpireOgel foundOgelCondition = getSpireOgelById(ogelID);
    switch (fieldName) {
      case "canList":
        foundOgelCondition.getSummary().setCanList(updateData);
        break;
      case "cantList":
        foundOgelCondition.getSummary().setCantList(updateData);
        break;
      case "mustList":
        foundOgelCondition.getSummary().setMustList(updateData);
        break;
      case "howToUseList":
        foundOgelCondition.getSummary().setHowToUseList(updateData);
        break;
      default:
        LOGGER.error("Update operation unsuccessful. Invalid data field name " + fieldName);
    }
    return foundOgelCondition;
  }
}
