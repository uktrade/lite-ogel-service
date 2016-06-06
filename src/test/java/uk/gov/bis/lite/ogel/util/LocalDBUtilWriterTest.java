package uk.gov.bis.lite.ogel.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.bis.lite.ogel.database.utility.LocalOgelDBUtil;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LocalDBUtilWriterTest {

  @Test
  public void writesLocalOgelListOut() throws IOException {
    final List<LocalOgel> localOgels = LocalOgelDBUtil.retrieveAllOgelsFromJSON();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(new File("updated-full-local-ogel.json"), localOgels);
  }
}
