package uk.gov.bis.lite.ogel.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;

import java.util.Base64;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class ControlCodeClient {

  private static final String CONTROL_CODES_PATH = "/control-codes";

  private final Client client;
  private final String url;
  private final String credentials;

  @Inject
  public ControlCodeClient(Client client,
                           @Named("controlCodeServiceUrl") String url,
                           @Named("controlCodeServiceCredentials") String credentials) {
    this.client = client;
    this.url = url;
    this.credentials = credentials;
  }

  public List<ControlCodeFullView> getAllControlCodes() {
    Response response = client.target(url)
        .path(CONTROL_CODES_PATH)
        .request()
        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes()))
        .get();

    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      return response.readEntity(new GenericType<List<ControlCodeFullView>>() {});
    } else {
      throw new WebApplicationException("Unable to get control code details from the control code service",
          Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  public BulkControlCodes bulkControlCodes(List<String> controlCodes) {
    WebTarget controlCodeServiceTarget = client.target(url).path("/bulk-control-codes");
    for (String controlCode : controlCodes) {
      controlCodeServiceTarget = controlCodeServiceTarget.queryParam("controlCode", controlCode);
    }
    Response response = controlCodeServiceTarget.request()
        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes()))
        .get();
    if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
      return response.readEntity(BulkControlCodes.class);
    } else {
      throw new WebApplicationException("Unable to get control code details from the control code service", Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

}
