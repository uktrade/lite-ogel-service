package uk.gov.bis.lite.ogel.client;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.ogel.model.BulkControlCodeCutDowns;
import uk.gov.bis.lite.ogel.model.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.model.ControlCodeCutDown;
import uk.gov.bis.lite.ogel.model.ControlCodeFullView;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ControlCodeClientTest extends JerseyTest {

  private ControlCodeClient controlCodeClient;

  private final static List<ControlCodeFullView> CONTROL_CODES = getControlCodes();

  private final static BulkControlCodeCutDowns BULK_CONTROL_CODES = getBulkControlCodes();

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
    public BulkControlCodeCutDowns bulkControlCodes() {
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
    LocalControlCodeCondition localControlCodeCondition = new LocalControlCodeCondition();
    localControlCodeCondition.setConditionDescriptionControlCodes(Arrays.asList("C1", "C2", "C3"));
    Response response = controlCodeClient.bulkControlCodes(localControlCodeCondition);

    assertThat(response.getStatus()).isEqualTo(200);
    ControlCodeConditionFullView controlCodeConditionFullView = (ControlCodeConditionFullView)response.getEntity();
    assertThat(controlCodeConditionFullView).isNotNull();
    assertThat(controlCodeConditionFullView.getConditionDescriptionControlCodes().getControlCodes().size()).isEqualTo(2);
    assertThat(controlCodeConditionFullView.getConditionDescriptionControlCodes().getMissingControlCodes().size()).isEqualTo(1);
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

  private static BulkControlCodeCutDowns getBulkControlCodes() {
    ControlCodeCutDown controlCodeCutDown = new ControlCodeCutDown();
    controlCodeCutDown.setControlCode("C1");
    ControlCodeCutDown controlCodeCutDow2 = new ControlCodeCutDown();
    controlCodeCutDown.setControlCode("C2");

    return new BulkControlCodeCutDowns(Arrays.asList(controlCodeCutDown, controlCodeCutDow2),
      Arrays.asList("111"));
  }

}