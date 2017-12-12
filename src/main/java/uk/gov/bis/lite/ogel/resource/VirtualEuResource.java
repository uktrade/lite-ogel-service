package uk.gov.bis.lite.ogel.resource;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.ogel.api.view.VirtualEuView;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.ApplicableOgelService;
import uk.gov.bis.lite.ogel.spire.SpireUtil;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
  public Response getVirtualEu(@NotNull @QueryParam("controlCode") String controlCode,
                               @NotNull @QueryParam("sourceCountry") String sourceCountry,
                               @NotEmpty @QueryParam("destinationCountry") List<String> destinationCountries) {

    List<SpireOgel> ogels = applicableOgelService.findOgel(controlCode,
        SpireUtil.stripCountryPrefix(destinationCountries),
        Collections.singletonList(ActivityType.DU_ANY));

    boolean found = ogels.stream().anyMatch(s -> s.getId().equalsIgnoreCase(virtualEuOgelId));

    VirtualEuView virtualEuView = new VirtualEuView();
    virtualEuView.setVirtualEu(found);
    if (found) {
      virtualEuView.setOgelId(virtualEuOgelId);
    }

    return Response.ok(virtualEuView).build();
  }
}
