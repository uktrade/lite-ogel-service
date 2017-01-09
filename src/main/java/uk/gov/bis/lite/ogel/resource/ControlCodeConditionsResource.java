package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.client.ControlCodeClient;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.BulkControlCodeCutDowns;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.factory.ViewFactory;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.validator.CheckLocalControlCodeConditionList;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/control-code-conditions")
@Produces(MediaType.APPLICATION_JSON)
public class ControlCodeConditionsResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControlCodeConditionsResource.class);

  private final SpireOgelService ogelService;
  private final LocalOgelService localOgelService;
  private final LocalControlCodeConditionService localControlCodeConditionService;
  private final ControlCodeClient controlCodeClient;

  @Inject
  public ControlCodeConditionsResource(SpireOgelService ogelService, LocalOgelService localOgelService,
                                       LocalControlCodeConditionService localControlCodeConditionService,
                                       ControlCodeClient controlCodeClient) {
    this.ogelService = ogelService;
    this.localOgelService = localOgelService;
    this.localControlCodeConditionService = localControlCodeConditionService;
    this.controlCodeClient = controlCodeClient;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<LocalControlCodeCondition> getAllControlCodeConditions() throws OgelNotFoundException {
    return localControlCodeConditionService.getAllControlCodeConditions();
  }

  @GET
  @Path("{ogelID}/{controlCode}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getOgelByOgelID(@NotNull @PathParam("ogelID") String ogelID,
                                              @NotNull @PathParam("controlCode") String controlCode) {
    LocalOgel localOgelFound = localOgelService.findLocalOgelById(ogelID);
    if (localOgelFound == null) {
      LOGGER.warn("Local OGEL Not Found for OGEL ID: {}", ogelID);
    }

    LocalControlCodeCondition localControlCodeConditions = localControlCodeConditionService.getLocalControlCodeConditionsByIdAndControlCode(ogelID, controlCode);

    // When no control code condition found return a 204
    if (localControlCodeConditions == null) {
      return Response.status(Response.Status.NO_CONTENT)
          .build();
    }

    if (localControlCodeConditions.getConditionDescriptionControlCodes().size() > 0) {
      return controlCodeClient.bulkControlCodes(localControlCodeConditions);
    }
    else {
      ControlCodeConditionFullView controlCodeConditionFullView = ViewFactory.createControlCodeCondition(localControlCodeConditions);
      return Response.ok(controlCodeConditionFullView).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOgelConditionsArray(@Auth PrincipalImpl user, @CheckLocalControlCodeConditionList List<LocalControlCodeCondition> ogelConditionsList)
  throws OgelNotFoundException {
    if (ogelConditionsList.isEmpty()) {
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, "Empty OGEL Conditions List")).build();
    }

    // Check OGEL IDs exist on SPIRE too or throw OgelNotFoundException
    ogelConditionsList.forEach(o -> ogelService.findSpireOgelById(o.getOgelID()));

    localControlCodeConditionService.insertControlCodeConditionList(ogelConditionsList);

    List<String> insertedOgelIDs = ogelConditionsList.stream().map(LocalControlCodeCondition::getOgelID).collect(Collectors.toList());
    return Response.status(Response.Status.CREATED).entity(
        getAllControlCodeConditions().stream().filter(ccc -> insertedOgelIDs.contains(ccc.getOgelID())).collect(Collectors.toList()))
        .type(MediaType.APPLICATION_JSON).build();
  }
}
