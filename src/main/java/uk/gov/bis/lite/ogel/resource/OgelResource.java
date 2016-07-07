package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.exception.SOAPParseException;
import uk.gov.bis.lite.ogel.model.OgelFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.ConditionType;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.validator.CheckLocalOgel;
import uk.gov.bis.lite.ogel.validator.CheckLocalOgelList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ogels")
@Produces(MediaType.APPLICATION_JSON)
public class OgelResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(OgelResource.class);

  private final SpireOgelService ogelService;
  private final LocalOgelService localOgelService;

  @Inject
  public OgelResource(SpireOgelService ogelService, LocalOgelService localOgelService) {
    this.ogelService = ogelService;
    this.localOgelService = localOgelService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<OgelFullView> getAllOgels()
      throws OgelNotFoundException, SOAPParseException {
    List<SpireOgel> allSpireOgels = ogelService.getAllOgels();
    return allSpireOgels.stream().map(so -> new OgelFullView(so, localOgelService.findLocalOgelById(so.getId())))
        .collect(Collectors.toList());
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public OgelFullView getOgelByOgelID(@NotNull @PathParam("id") String ogelId) {
    SpireOgel foundSpireOgel = ogelService.findSpireOgelById(ogelId);
    LocalOgel localOgelFound = localOgelService.findLocalOgelById(ogelId);
    if (localOgelFound == null) {
      LOGGER.warn("Local Ogel Not Found for ogel ID: {}", ogelId);
    }
    return new OgelFullView(foundSpireOgel, localOgelFound);
  }

  @PUT
  @Path("{id}/summary/{conditionFieldName}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateOgelCondition(
      @Auth PrincipalImpl user,
      @NotNull @PathParam("id") String ogelId,
      @NotNull @PathParam("conditionFieldName") String conditionFieldName,
      String message) throws
      IOException {
    try {
      ConditionType.fromString(conditionFieldName);
    } catch (IllegalArgumentException e) {
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<String> updateConditionDataList = mapper.readValue(message,
          mapper.getTypeFactory().constructCollectionType(List.class, String.class));
      localOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName);
      return Response.accepted(getOgelByOgelID(ogelId)).build();
    } catch (JsonProcessingException e) {
      LOGGER.error("Badly formed Json request body {}", message, e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    } catch (IOException e) {
      LOGGER.error("An error occurred processing the PUT request for ogel with ID {}", ogelId, e);
      throw new RuntimeException("An error occurred updating the Ogel.", e);
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred processing updating the ogel with ID {}", ogelId, e);
      throw new RuntimeException("Update Request Unsuccessful ", e);
    }
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOrUpdateOgel(@Auth PrincipalImpl user,
                                     @NotNull @PathParam("id") String ogelId,
                                     @CheckLocalOgel LocalOgel localOgel) {
    try {
      if (localOgel.getName() == null && localOgel.getSummary() == null) {
        return Response.status(BAD_REQUEST.getStatusCode()).entity("Invalid or empty property name found in the request json").build();
      }
      ogelService.findSpireOgelById(ogelId);

      localOgel.setId(ogelId);
      localOgelService.insertOrUpdateOgel(localOgel);
      return Response.status(Response.Status.CREATED).entity(getOgelByOgelID(ogelId)).type(MediaType.APPLICATION_JSON).build();
    } catch (OgelNotFoundException e) {
      LOGGER.error("There is no ogel found with ID {}", ogelId);
      return Response.status(INTERNAL_SERVER_ERROR.getStatusCode()).entity(new ErrorMessage(e.getMessage())).build();
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred processing handling the insertNewLocalOgel new or update ogel request with ID {}", ogelId, e);
      throw new RuntimeException("Request Unsuccessful ", e);
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOgelArray(@Auth PrincipalImpl user, @CheckLocalOgelList List<LocalOgel> ogelList) {
    try {
      localOgelService.insertOgelList(ogelList);
    } catch (SQLException e) {
      LOGGER.error("A database error occurred persisting new local ogel list data into database", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred ", e);
      return Response.status(INTERNAL_SERVER_ERROR.getStatusCode()).entity(new ErrorMessage(500, e.getMessage())).build();
    }
    return Response.status(Response.Status.CREATED).entity(getAllOgels()).type(MediaType.APPLICATION_JSON).build();
  }
}
