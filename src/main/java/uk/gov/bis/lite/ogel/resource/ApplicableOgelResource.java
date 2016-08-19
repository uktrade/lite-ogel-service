package uk.gov.bis.lite.ogel.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.ApplicableOgelView;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

  private final SpireOgelService spireOgelService;
  private final LocalOgelService localOgelService;

  @Inject
  public ApplicableOgelResource(SpireOgelService spireOgelService, LocalOgelService localOgelService) {
    this.spireOgelService = spireOgelService;
    this.localOgelService = localOgelService;
  }

  @GET
  @Timed
  @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
  public Response getOgelList(@NotNull(message = "controlCode required") @QueryParam("controlCode") String controlCode,
                              @NotNull(message = "sourceCountry required") @QueryParam("sourceCountry") String sourceCountry,
                              @NotNull(message = "destinationCountry required") @QueryParam("destinationCountry") String destinationCountry,
                              @QueryParam("activityType") List<String> activityTypesParam) {

    for (String activityTypeParam : activityTypesParam) {
      if (!ActivityType.typeExists(activityTypeParam)) {
        throw new WebApplicationException("Invalid activityType: " + activityTypeParam, 400);
      }
    }

    if (activityTypesParam.size() == 0) {
      throw new WebApplicationException("At least one activityType must be provided", 400);
    }

    List<ActivityType> activityTypes = activityTypesParam.stream().map(ActivityType::valueOf).collect(Collectors.toList());

    try {
      List<ApplicableOgelView> applicableOgels = spireOgelService
          .findOgel(controlCode, destinationCountry, activityTypes)
          .stream()
          .map(e -> ApplicableOgelView.create(e, localOgelService.findLocalOgelById(e.getId())))
          .collect(Collectors.toList());

      OutboundMessageContext messageContext = new OutboundMessageContext();
      messageContext.setMediaType(MediaType.APPLICATION_JSON_TYPE);
      messageContext.setEntity(applicableOgels);

      return new OutboundJaxrsResponse(Response.Status.OK, messageContext);

    } catch (RuntimeException e) {
      return Response.status(500).entity(new ErrorMessage(e.getMessage())).build();
    }
  }
}
