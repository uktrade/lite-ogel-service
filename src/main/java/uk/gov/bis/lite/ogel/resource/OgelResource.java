package uk.gov.bis.lite.ogel.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.common.auth.basic.Roles;
import uk.gov.bis.lite.common.auth.basic.User;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.factory.ViewFactory;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.ConditionType;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.validator.CheckLocalOgel;
import uk.gov.bis.lite.ogel.validator.CheckLocalOgelList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ogels")
@Produces(MediaType.APPLICATION_JSON)
public class OgelResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(OgelResource.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final CollectionType LIST_STRING_TYPE = TypeFactory.defaultInstance().constructCollectionType(List.class, String.class);

  private final SpireOgelService spireOgelService;
  private final LocalOgelService localOgelService;

  @Inject
  public OgelResource(SpireOgelService spireOgelService, LocalOgelService localOgelService) {
    this.spireOgelService = spireOgelService;
    this.localOgelService = localOgelService;
  }

  @RolesAllowed(Roles.SERVICE)
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<OgelFullView> getAllOgels(@Auth User user) {
    List<SpireOgel> allSpireOgels = spireOgelService.getAllOgels();
    return allSpireOgels
        .stream().map(so -> ViewFactory.createOgel(so, localOgelService.findLocalOgelById(so.getId()).orElse(null)))
        .collect(Collectors.toList());
  }

  @RolesAllowed(Roles.SERVICE)
  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public OgelFullView getOgelByOgelID(@Auth User user,
                                      @NotNull @PathParam("id") String ogelId) {
    Optional<SpireOgel> foundSpireOgelOptional = spireOgelService.findSpireOgelById(ogelId);
    if (!foundSpireOgelOptional.isPresent()) {
      throw new OgelNotFoundException(ogelId);
    }
    Optional<LocalOgel> localOgelFound = localOgelService.findLocalOgelById(ogelId);
    if (!localOgelFound.isPresent()) {
      LOGGER.warn("Local Ogel Not Found for ogel ID: {}", ogelId);
    }
    return ViewFactory.createOgel(foundSpireOgelOptional.get(), localOgelFound.orElse(null));
  }

  @RolesAllowed(Roles.ADMIN)
  @PUT
  @Path("{id}/summary/{conditionFieldName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateOgelCondition(@Auth User user,
                                      @NotNull @PathParam("id") String ogelId,
                                      @NotNull @PathParam("conditionFieldName") String conditionFieldName,
                                      String message) {

    if (ConditionType.fromString(conditionFieldName) == null) {
      throw new WebApplicationException("Unknown conditionFieldName " + conditionFieldName, Response.Status.BAD_REQUEST);
    }

    if (!spireOgelService.findSpireOgelById(ogelId).isPresent()) {
      throw new OgelNotFoundException(ogelId);
    }

    List<String> updateConditionDataList;
    try {
      updateConditionDataList = MAPPER.readValue(message, LIST_STRING_TYPE);
    } catch (IOException exception) {
      LOGGER.error("Badly formed json request body {}", message, exception);
      throw new WebApplicationException("Badly formed json request body", Response.Status.BAD_REQUEST);
    }

    localOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName);
    return Response.accepted(getOgelByOgelID(user, ogelId)).build();
  }

  @RolesAllowed(Roles.ADMIN)
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response insertOrUpdateOgel(@Auth User user,
                                     @NotNull @PathParam("id") String ogelId,
                                     @CheckLocalOgel LocalOgel localOgel) {
    if (!spireOgelService.findSpireOgelById(ogelId).isPresent()) {
      throw new OgelNotFoundException(ogelId);
    }

    localOgel.setId(ogelId);
    localOgelService.insertOrUpdateOgel(localOgel);
    return Response.status(Response.Status.CREATED).entity(getOgelByOgelID(user, ogelId)).build();
  }

  @RolesAllowed(Roles.ADMIN)
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response insertOgelArray(@Auth User user,
                                  @NotEmpty @CheckLocalOgelList List<LocalOgel> ogelList) {

    ogelList.forEach(o -> {
      if (!spireOgelService.findSpireOgelById(o.getId()).isPresent()) {
        throw new OgelNotFoundException(o.getId());
      }
    });

    localOgelService.insertOgelList(ogelList);
    List<String> updatedOgelIds = ogelList.stream().map(LocalOgel::getId).collect(Collectors.toList());
    return Response.status(Response.Status.CREATED).entity(
        getAllOgels(user).stream().filter(o -> updatedOgelIds.contains(o.getId())).collect(Collectors.toList()))
        .build();
  }

  @RolesAllowed(Roles.ADMIN)
  @DELETE
  public void deleteAllOgels(@Auth User user) {
    localOgelService.deleteAllOgels();
  }

  @RolesAllowed(Roles.ADMIN)
  @DELETE
  @Path("{id}")
  public void deleteOgelById(@Auth User user,
                             @NotNull @PathParam("id") String ogelId) {
    localOgelService.deleteOgelById(ogelId);
  }

}
