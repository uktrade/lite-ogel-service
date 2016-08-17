package uk.gov.bis.lite.ogel.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
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

  private final SpireOgelService ogelService;

  @Inject
  public ApplicableOgelResource(SpireOgelService ogelService) {
    this.ogelService = ogelService;
  }

  @GET
  @Timed
  @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
  public Response getOgelList(@NotNull(message = "controlCode required") @QueryParam("controlCode") String controlCode,
                              @NotNull(message = "sourceCountry required") @QueryParam("sourceCountry") String sourceCountry,
                              @NotNull(message = "destinationCountry required") @QueryParam("destinationCountry") String destinationCountry,
                              @QueryParam("activityType") List<String> activityTypes) {
    for (String category : activityTypes) {
      if (!categoryTypeExists(category)) {
        throw new WebApplicationException("Invalid Activity Type for category: " + category, 400);
      }
    }

    if (activityTypes.size() == 0) {
      throw new WebApplicationException("At least one activityType must be provided", 400);
    }

    final List<CategoryType> categoryTypes = activityTypes.stream().map(CategoryType::valueOf).collect(Collectors.toList());
    try {
      final List<SpireOgel> matchedSpireOgels = ogelService.findOgel(controlCode, destinationCountry, categoryTypes);
      if (matchedSpireOgels != null) {
        if (matchedSpireOgels.isEmpty()) {
          return OutboundJaxrsResponse.noContent().build();
        }
        OutboundMessageContext messageContext = new OutboundMessageContext();
        messageContext.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        messageContext.setEntity(matchedSpireOgels);
        return new OutboundJaxrsResponse(Response.Status.OK, messageContext);
      }
      return OutboundJaxrsResponse.serverError().build();
    } catch (RuntimeException e) {
      return Response.status(500).entity(new ErrorMessage(e.getMessage())).build();
    }
  }

  private Boolean categoryTypeExists(String activityType) {
    for (CategoryType type : CategoryType.values()) {
      if (type.name().equals(activityType)) {
        return true;
      }
    }
    return false;
  }
}
