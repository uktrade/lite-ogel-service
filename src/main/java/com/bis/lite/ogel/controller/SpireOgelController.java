package com.bis.lite.ogel.controller;

import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.SpireOgel;
import com.bis.lite.ogel.service.SpireOgelService;
import com.codahale.metrics.annotation.Timed;

import com.google.inject.Inject;

import io.dropwizard.jersey.caching.CacheControl;

import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/applicable-ogels")
@Produces(MediaType.APPLICATION_JSON)
public class SpireOgelController {

    private final SpireOgelService ogelService;

    @Inject
    public SpireOgelController(SpireOgelService ogelService) {
        this.ogelService = ogelService;
    }

    @GET
    @Timed
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public Response getOgelList(@NotNull @QueryParam("controlCode") String controlCode,
                                @NotNull @QueryParam("sourceCountry") String sourceCountry,
                                @NotNull @QueryParam("destinationCountry") String destinationCountry,
                                @NotNull @QueryParam("activityType") List<String> activityTypes) {
        for (String category : activityTypes) {
            if (!categoryTypeExists(category)) {
                throw new WebApplicationException("Invalid Activity Type for category: " + category, 400);
            }
        }
        final List<CategoryType> categoryTypes = activityTypes.stream().map(a -> CategoryType.valueOf(a)).collect(Collectors.toList());
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
    }

    private static final Boolean categoryTypeExists(String activityType) {
        for (CategoryType type : CategoryType.values()) {
            if (type.name().equals(activityType)) {
                return true;
            }
        }
        return false;
    }
}
