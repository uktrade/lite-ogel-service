package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.dropwizard.auth.Auth;
import uk.gov.bis.lite.common.auth.basic.Roles;
import uk.gov.bis.lite.common.auth.basic.User;
import uk.gov.bis.lite.ogel.api.view.ValidateView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/validate")
@Produces(MediaType.APPLICATION_JSON)
public class ValidateResource {

  private final LocalOgelService localOgelService;
  private final SpireOgelService spireOgelService;
  private final String virtualEuOgelId;

  @Inject
  public ValidateResource(LocalOgelService localOgelService, SpireOgelService spireOgelService,
                          @Named("virtualEuOgelId") String virtualEuOgelId) {
    this.localOgelService = localOgelService;
    this.spireOgelService = spireOgelService;
    this.virtualEuOgelId = virtualEuOgelId;
  }

  @RolesAllowed(Roles.ADMIN)
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response validate(@Auth User user) {

    List<String> localOgelIds = localOgelService.getAllLocalOgels().stream()
        .map(LocalOgel::getId)
        .collect(Collectors.toList());

    List<String> spireOgelIds = spireOgelService.getAllOgels().stream()
        .map(SpireOgel::getId)
        .filter(c -> !c.equals(virtualEuOgelId))
        .collect(Collectors.toList());

    List<String> unmatchedLocalOgelIds = localOgelIds.stream()
        .filter(o -> !spireOgelIds.contains(o))
        .collect(Collectors.toList());

    List<String> unmatchedSpireOgelIds = spireOgelIds.stream().
        filter(o -> !localOgelIds.contains(o))
        .collect(Collectors.toList());

    ValidateView validateView = new ValidateView();
    validateView.setUnmatchedLocalOgelIds(unmatchedLocalOgelIds);
    validateView.setUnmatchedSpireOgelIds(unmatchedSpireOgelIds);

    if (validateView.getUnmatchedLocalOgelIds().isEmpty() && validateView.getUnmatchedSpireOgelIds().isEmpty()) {
      return Response.status(OK.getStatusCode()).entity(validateView).build();
    } else {
      return Response.status(INTERNAL_SERVER_ERROR.getStatusCode()).entity(validateView).build();
    }
  }

}
