package uk.gov.bis.lite.ogel.resource;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.gov.bis.lite.ogel.api.view.VirtualEuView;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.ApplicableOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceImpl;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/virtual-eu")
@Produces(MediaType.APPLICATION_JSON)
public class VirtualEuResource {

  private final ApplicableOgelService applicableOgelService;
  private final String virtualEuOgelId;

  @Inject
  public VirtualEuResource(ApplicableOgelService applicableOgelService, @Named("virtualEuOgelId") String virtualEuOgelId) {
    this.applicableOgelService = applicableOgelService;
    this.virtualEuOgelId = virtualEuOgelId;
  }

  @GET
  public Response getVirtualEu(@NotNull(message = "controlCode required") @QueryParam("controlCode") String controlCode,
                              @NotNull(message = "sourceCountry required") @QueryParam("sourceCountry") String sourceCountry,
                              @QueryParam("destinationCountry") List<String> destinationCountries) {

    if (destinationCountries.size() == 0) {
      throw new WebApplicationException("At least one destinationCountry must be provided", 400);
    }

    List<SpireOgel> ogels = applicableOgelService.findOgel(controlCode, SpireOgelServiceImpl.stripCountryPrefix(destinationCountries),
        ActivityType.DU_ANY.asList());
    boolean found = ogels.stream().filter(s -> s.getId().equalsIgnoreCase(virtualEuOgelId)).findFirst().isPresent();

    VirtualEuView virtualEuView = new VirtualEuView();
    virtualEuView.setVirtualEu(found);
    if (found) {
      virtualEuView.setOgelId(virtualEuOgelId);
    }

    return Response.ok(virtualEuView).build();
  }
}
