package uk.gov.bis.lite.ogel.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.gov.bis.lite.ogel.model.BulkControlCodeCutDowns;
import uk.gov.bis.lite.ogel.model.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.model.ControlCodeFullView;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

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

  public Response bulkControlCodes(LocalControlCodeCondition localControlCodeCondition) {
    WebTarget controlCodeServiceTarget = client.target(controlCodeServiceUrl).path("/bulk-control-codes");
    for (String controlCode : localControlCodeCondition.getConditionDescriptionControlCodes()) {
      controlCodeServiceTarget = controlCodeServiceTarget.queryParam("controlCode", controlCode);
    }

    if (localControlCodeCondition.getConditionDescriptionControlCodes().size() > 0) {

      try {
        Response response = controlCodeServiceTarget.request().get();
        String controlCodeServiceResponse = response.readEntity(String.class);
        BulkControlCodeCutDowns bulkControlCodeCutDowns = new ObjectMapper().readValue(controlCodeServiceResponse, BulkControlCodeCutDowns.class);
        // Valid responses should be OK or Partial Content when one or more of the control codes could not be found
        if (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode()) {
          return Response.status(response.getStatus()).entity(new ControlCodeConditionFullView(localControlCodeCondition, bulkControlCodeCutDowns)).build();
        } else {
          throw new WebApplicationException("Unable to get control code details from the control code service", Response.Status.INTERNAL_SERVER_ERROR);
        }
      } catch (IOException e) {
        throw new WebApplicationException("Unable to get control code details from the control code service", e, Response.Status.INTERNAL_SERVER_ERROR);
      }
    } else {
      return Response.ok(new ControlCodeConditionFullView(localControlCodeCondition, null)).build();
    }

  }


}
