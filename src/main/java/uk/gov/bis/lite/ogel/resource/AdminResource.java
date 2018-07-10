package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);

  private final LocalOgelService localOgelService;
  private final SpireOgelService spireOgelService;
  private final String virtualEuOgelId;

  @Inject
  public AdminResource(LocalOgelService localOgelService, SpireOgelService spireOgelService,
                       @Named("virtualEuOgelId") String virtualEuOgelId) {
    this.localOgelService = localOgelService;
    this.spireOgelService = spireOgelService;
    this.virtualEuOgelId = virtualEuOgelId;
  }

  @RolesAllowed(Roles.SERVICE)
  @GET
  @Path("/ping")
  public Response ping(@Auth User user) {
    LOGGER.info("Admin ping received, responding with 200 OK");
    return Response.status(Response.Status.OK).build();
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
