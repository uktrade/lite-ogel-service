package uk.gov.bis.lite.ogel.database;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public class LocalOgelFlatFileImplTest {

  private LocalOgelDAO populator = new LocalOgelFlatFileImpl();

  @Test
  public void testDatabasePopulation(){
    final List<LocalOgel> localOgels = populator.getAllLocalOgels();
    assertTrue(!localOgels.isEmpty());
    final LocalOgel firstLocalOgel = localOgels.get(0);
    assertEquals("Open general export licence (chemicals)", firstLocalOgel.getName());
    assertEquals("16", firstLocalOgel.getId());
    assertTrue(firstLocalOgel.getSummary().getCantList().contains("Export more than 20kg of goods in a single shipment"));
  }
}
