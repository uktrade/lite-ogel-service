package uk.gov.bis.lite.ogel.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.caching.CacheControl;
import org.apache.commons.lang3.EnumUtils;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.common.auth.basic.Roles;
import uk.gov.bis.lite.common.auth.basic.User;
import uk.gov.bis.lite.ogel.api.view.ApplicableOgelView;
import uk.gov.bis.lite.ogel.factory.ViewFactory;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.ApplicableOgelService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.spire.SpireUtil;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/applicable-ogels")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicableOgelResource {

  private final ApplicableOgelService applicableOgelService;
  private final LocalOgelService localOgelService;
  private final String virtualEuOgelId;

  @Inject
  public ApplicableOgelResource(ApplicableOgelService applicableOgelService, LocalOgelService localOgelService,
                                @Named("virtualEuOgelId") String virtualEuOgelId) {
    this.applicableOgelService = applicableOgelService;
    this.localOgelService = localOgelService;
    this.virtualEuOgelId = virtualEuOgelId;
  }

  @RolesAllowed(Roles.SERVICE)
  @GET
  @Timed
  @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
  @Produces(MediaType.APPLICATION_JSON)
  public Response getOgelList(@Auth User user,
                              @NotNull @QueryParam("controlCode") String controlCode,
                              @NotNull @QueryParam("sourceCountry") String sourceCountry,
                              @NotEmpty @QueryParam("destinationCountry") List<String> destinationCountries,
                              @NotEmpty @QueryParam("activityType") List<String> activityTypesParam) {

    for (String activityTypeParam : activityTypesParam) {
      if (EnumUtils.getEnum(ActivityType.class, activityTypeParam) == null) {
        throw new WebApplicationException("Invalid activityType: " + activityTypeParam, Response.Status.BAD_REQUEST);
      }
    }

    List<ActivityType> activityTypes = activityTypesParam.stream()
        .map(param -> EnumUtils.getEnum(ActivityType.class, param))
        .collect(Collectors.toList());

    List<ApplicableOgelView> applicableOgels = applicableOgelService
        .findOgel(controlCode, SpireUtil.stripCountryPrefix(destinationCountries), activityTypes)
        .stream()
        .filter(e -> !virtualEuOgelId.equals(e.getId()))
        .sorted(Comparator.comparing(SpireOgel::getRanking).thenComparing(SpireOgel::getId))
        .map(e -> ViewFactory.createApplicableOgel(e, localOgelService.findLocalOgelById(e.getId()).orElse(null)))
        .collect(Collectors.toList());

    return Response.ok(applicableOgels).build();
  }
}
