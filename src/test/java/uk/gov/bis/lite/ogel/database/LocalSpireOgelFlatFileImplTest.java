package uk.gov.bis.lite.ogel.database;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import uk.gov.bis.lite.ogel.database.dao.LocalSpireOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;

import java.util.List;

public class LocalSpireOgelFlatFileImplTest {

  LocalSpireOgelDAO populator = new LocalSpireOgelFlatFileImpl();

  @Test
  public void testDatabasePopulation(){
    final List<LocalSpireOgel> localSpireOgels = populator.getAllLocalSpireOgels();
    assertTrue(!localSpireOgels.isEmpty());
    final LocalSpireOgel firstLocalSpireOgel = localSpireOgels.get(0);
    assertEquals("Open general export licence (chemicals)", firstLocalSpireOgel.getName());
    assertEquals("16", firstLocalSpireOgel.getId());
    assertTrue(firstLocalSpireOgel.getSummary().getCantList().contains("Export more than 20kg of goods in a single shipment"));
  }
}
