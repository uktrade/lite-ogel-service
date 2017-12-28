package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.common.auth.basic.Roles;
import uk.gov.bis.lite.common.auth.basic.User;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.api.view.ValidateView;
import uk.gov.bis.lite.ogel.client.ControlCodeClient;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  private final LocalOgelService localOgelService;
  private final SpireOgelService spireOgelService;
  private final LocalControlCodeConditionService controlCodeConditionService;
  private final ControlCodeClient controlCodeClient;
  private final String virtualEuOgelId;

  @Inject
  public AdminResource(LocalOgelService localOgelService, SpireOgelService spireOgelService,
                       LocalControlCodeConditionService controlCodeConditionService,
                       ControlCodeClient controlCodeClient,
                       @Named("virtualEuOgelId") String virtualEuOgelId) {
    this.localOgelService = localOgelService;
    this.spireOgelService = spireOgelService;
    this.controlCodeConditionService = controlCodeConditionService;
    this.controlCodeClient = controlCodeClient;
    this.virtualEuOgelId = virtualEuOgelId;
  }

  @RolesAllowed(Roles.ADMIN)
  @GET
  @Path("/validate")
  @Produces(MediaType.APPLICATION_JSON)
  public Response validate(@Auth User user) {

    List<String> localOgelIds = localOgelService.getAllLocalOgels().stream()
        .map(LocalOgel::getId)
        .collect(Collectors.toList());

    List<String> spireOgelIds = spireOgelService.getAllOgels().stream()
        .map(SpireOgel::getId)
        .filter(c -> !c.equals(virtualEuOgelId))
        .collect(Collectors.toList());

    List<LocalControlCodeCondition> controlCodeConditions = controlCodeConditionService.getAllControlCodeConditions();

    List<String> externalControlCodes = getExternalControlCodes().stream()
        .map(ControlCodeFullView::getControlCode)
        .collect(Collectors.toList());

    List<String> unmatchedLocalOgelIds = localOgelIds.stream()
        .filter(o -> !spireOgelIds.contains(o))
        .collect(Collectors.toList());

    List<String> unmatchedSpireOgelIds = spireOgelIds.stream().
        filter(o -> !localOgelIds.contains(o))
        .collect(Collectors.toList());


    SetMultimap<String, String> unmatchedControlCodes = HashMultimap.create();
    controlCodeConditions.forEach(c ->
        unmatchedControlCodes.putAll(c.getOgelID(), checkControlCodes(c, externalControlCodes)));

    ValidateView validateView = new ValidateView();
    validateView.setUnmatchedControlCodes(unmatchedControlCodes.asMap());
    validateView.setUnmatchedLocalOgelIds(unmatchedLocalOgelIds);
    validateView.setUnmatchedSpireOgelIds(unmatchedSpireOgelIds);

    if (validateView.getUnmatchedControlCodes().isEmpty()
        && validateView.getUnmatchedLocalOgelIds().isEmpty() && validateView.getUnmatchedSpireOgelIds().isEmpty()) {
      return Response.status(OK.getStatusCode()).entity(validateView).build();
    } else {
      return Response.status(INTERNAL_SERVER_ERROR.getStatusCode()).entity(validateView).build();
    }
  }

  private List<String> checkControlCodes(LocalControlCodeCondition controlCodeCondition,
                                         List<String> externalControlCodes) {
    List<String> unmatchedControlCodes = new ArrayList<>();
    String controlCode = controlCodeCondition.getControlCode();
    if (!externalControlCodes.contains(controlCode)) {
      unmatchedControlCodes.add(controlCode);
    }

    List<String> controlCodes = controlCodeCondition.getConditionDescriptionControlCodes().stream()
        .filter(StringUtils::isNotBlank)
        .filter(c -> !externalControlCodes.contains(c))
        .collect(Collectors.toList());

    unmatchedControlCodes.addAll(controlCodes);

    return unmatchedControlCodes;
  }

  private List<ControlCodeFullView> getExternalControlCodes() {
    return controlCodeClient.getAllControlCodes();
  }

}
