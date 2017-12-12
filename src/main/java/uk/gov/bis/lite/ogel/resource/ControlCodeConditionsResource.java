package uk.gov.bis.lite.ogel.resource;

import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView.ConditionDescriptionControlCodes;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.ControlCodeConditionsService;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.validator.CheckLocalControlCodeConditionList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/control-code-conditions")
@Produces(MediaType.APPLICATION_JSON)
public class ControlCodeConditionsResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControlCodeConditionsResource.class);

  private final SpireOgelService ogelService;
  private final LocalOgelService localOgelService;
  private final LocalControlCodeConditionService localControlCodeConditionService;
  private final ControlCodeConditionsService controlCodeConditionsService;

  @Inject
  public ControlCodeConditionsResource(SpireOgelService ogelService, LocalOgelService localOgelService,
                                       LocalControlCodeConditionService localControlCodeConditionService,
                                       ControlCodeConditionsService controlCodeConditionsService) {
    this.ogelService = ogelService;
    this.localOgelService = localOgelService;
    this.localControlCodeConditionService = localControlCodeConditionService;
    this.controlCodeConditionsService = controlCodeConditionsService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<LocalControlCodeCondition> getAllControlCodeConditions() throws OgelNotFoundException {
    return localControlCodeConditionService.getAllControlCodeConditions();
  }

  @GET
  @Path("{ogelID}/{controlCode}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getControlCodeConditionById(@NotNull @PathParam("ogelID") String ogelID,
                                              @NotNull @PathParam("controlCode") String controlCode) {
    Optional<LocalOgel> localOgelFound = localOgelService.findLocalOgelById(ogelID);
    if (!localOgelFound.isPresent()) {
      LOGGER.warn("Local OGEL Not Found for OGEL ID: {}", ogelID);
    }

    Optional<ControlCodeConditionFullView> codeConditionFullViewOpt = controlCodeConditionsService.findControlCodeConditions(ogelID, controlCode);

    if (codeConditionFullViewOpt.isPresent()) {
      Status status;
      ConditionDescriptionControlCodes conditionDescriptionControlCodes = codeConditionFullViewOpt.get().getConditionDescriptionControlCodes();
      if (conditionDescriptionControlCodes != null && !conditionDescriptionControlCodes.getMissingControlCodes().isEmpty()) {
        // If missing control codes, then return partial content status
        status = Status.PARTIAL_CONTENT;
      } else {
        status = Status.OK;
      }
      return Response.status(status).entity(codeConditionFullViewOpt.get()).build();
    } else {
      return Response.status(Status.NO_CONTENT).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response insertOgelConditionsArray(@Auth PrincipalImpl user,
                                            @NotEmpty @CheckLocalControlCodeConditionList List<LocalControlCodeCondition> ogelConditionsList)
      throws OgelNotFoundException {

    // Check OGEL IDs exist on SPIRE too or throw OgelNotFoundException
    ogelConditionsList.forEach(o -> {
      if (!ogelService.findSpireOgelById(o.getOgelID()).isPresent()) {
        throw new OgelNotFoundException(o.getOgelID());
      }
    });

    localControlCodeConditionService.insertControlCodeConditionList(ogelConditionsList);

    List<String> insertedOgelIDs = ogelConditionsList.stream().map(LocalControlCodeCondition::getOgelID).collect(Collectors.toList());
    return Response.status(Status.CREATED).entity(
        getAllControlCodeConditions().stream().filter(ccc -> insertedOgelIDs.contains(ccc.getOgelID())).collect(Collectors.toList()))
        .build();
  }

  @DELETE
  public void deleteControlCodeConditions(@Auth PrincipalImpl user) {
    localControlCodeConditionService.deleteControlCodeConditions();
  }
}
