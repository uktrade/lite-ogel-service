package uk.gov.bis.lite.ogel.database.utility;

import uk.gov.bis.lite.ogel.database.ListJsonMapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgelLookUp;

import java.io.IOException;
import java.util.List;

public class LocalOgelDBUtil {
  private static final String LOCAL_OGEL_CONDITION_DATA_FILE = "ogel-condition-data.json";
  private static final String LOCAL_OGEL_LOOKUP_DATA_FILE = "ogel-lookup-data.json";

  public static List<LocalOgel> retrieveAllOgelsFromJSON() throws IOException {
    List<LocalOgel> localOgelList = (List<LocalOgel>)
        new ListJsonMapper().mapToListFromJson(LOCAL_OGEL_CONDITION_DATA_FILE, LocalOgel.class);

    List<LocalOgelLookUp> localOgelLookUpList = (List<LocalOgelLookUp>)
        new ListJsonMapper().mapToListFromJson(LOCAL_OGEL_LOOKUP_DATA_FILE, LocalOgelLookUp.class);

    for (LocalOgel localOgel : localOgelList) {
      localOgelLookUpList.stream().filter(localLookUp -> localOgel.getName().equalsIgnoreCase(localLookUp.getName()))
          .forEach(localLookUp -> localOgel.setId(localLookUp.getId()));
    }
    return localOgelList;
  }
}
