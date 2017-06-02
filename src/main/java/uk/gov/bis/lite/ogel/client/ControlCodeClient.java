package uk.gov.bis.lite.ogel.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;

import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class ControlCodeClient {

  private static final String CONTROL_CODES_PATH = "/control-codes";

  private final Client client;
  private final String controlCodeServiceUrl;

  @Inject
  public ControlCodeClient(Client client, @Named("controlCodeServiceUrl") String controlCodeServiceUrl) {
    this.client = client;
    this.controlCodeServiceUrl = controlCodeServiceUrl;
  }

  public List<ControlCodeFullView> getAllControlCodes() {
    WebTarget webTarget = client.target(controlCodeServiceUrl).path(CONTROL_CODES_PATH);
    Response response = webTarget.request().get();

    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      return response.readEntity(new GenericType<List<ControlCodeFullView>>() {
      });
    } else {
      throw new WebApplicationException("Unable to get control code details from the control code service",
        Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  public BulkControlCodes bulkControlCodes(List<String> controlCodes) {
    WebTarget controlCodeServiceTarget = client.target(controlCodeServiceUrl).path("/bulk-control-codes");
    for (String controlCode : controlCodes) {
      controlCodeServiceTarget = controlCodeServiceTarget.queryParam("controlCode", controlCode);
    }
    try {
      Response response = controlCodeServiceTarget.request().get();
      return response.readEntity(BulkControlCodes.class);
    }
    catch (ProcessingException e) {
      throw new WebApplicationException("Unable to get control code details from the control code service", e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

}
