package uk.gov.bis.lite.ogel.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

public class ControlCodeClientTest extends JerseyTest {

  private ControlCodeClient controlCodeClient;

  private final static List<ControlCodeFullView> CONTROL_CODES = getControlCodes();

  private final static BulkControlCodes BULK_CONTROL_CODES = getBulkControlCodes();

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
   controlCodeClient = new ControlCodeClient(getClient(), "");
  }

  @Produces({MediaType.APPLICATION_JSON})
  @Path("/")
  public static class TestResource {
    @GET
    @Path("control-codes")
    public List<ControlCodeFullView> controlCodes() {
      return CONTROL_CODES;
    }

    @GET
    @Path("bulk-control-codes")
    public BulkControlCodes bulkControlCodes() {
      return BULK_CONTROL_CODES;
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(TestResource.class);
  }

  @Test
  public void shouleGetAllControlCodes() {
    List<ControlCodeFullView> result = controlCodeClient.getAllControlCodes();

    assertThat(result).isEqualTo(result);
  }

  @Test
  public void shouldGetBulkControlCodes() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(Arrays.asList("C1", "C2", "C3"));
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews()).extracting(e -> e.getControlCode()).containsOnly("C1", "C2");
    assertThat(bulkControlCodes.getMissingControlCodes().get(0)).isEqualTo("C3");
  }

  private static List<ControlCodeFullView> getControlCodes() {
    List<ControlCodeFullView> controlCodes = new ArrayList<>();
    ControlCodeFullView controlCodeFullView = new ControlCodeFullView();
    controlCodeFullView.setControlCode("C1");
    controlCodes.add(controlCodeFullView);
    controlCodeFullView = new ControlCodeFullView();
    controlCodeFullView.setControlCode("C2");
    controlCodes.add(controlCodeFullView);
    return controlCodes;
  }

  private static BulkControlCodes getBulkControlCodes() {
    ControlCodeFullView controlCodeFullView = new ControlCodeFullView();
    controlCodeFullView.setControlCode("C1");
    ControlCodeFullView controlCodeFullView2 = new ControlCodeFullView();
    controlCodeFullView2.setControlCode("C2");

    BulkControlCodes bulkControlCodes = new BulkControlCodes();
    bulkControlCodes.setControlCodeFullViews(Arrays.asList(controlCodeFullView, controlCodeFullView2));
    bulkControlCodes.setMissingControlCodes(Arrays.asList("C3"));
    return bulkControlCodes;
  }

}