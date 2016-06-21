package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.database.exception.SOAPParseException;
import uk.gov.bis.lite.ogel.model.OgelFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    List<LocalOgel> allLocalOgels = localOgelService.getAllLocalOgels();
    return allLocalOgels.stream()
        .map(lo -> new OgelFullView(ogelService.findSpireOgelByIdOrReturnNull(lo.getId()), lo)).collect(Collectors.toList());
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public OgelFullView getOgelByOgelID(@NotNull @PathParam("id") String ogelId)
      throws OgelNotFoundException, SOAPParseException {
    SpireOgel foundSpireOgel = ogelService.findSpireOgelById(ogelId);
    LocalOgel localOgelFound = localOgelService.findLocalOgelById(ogelId);
    if(localOgelFound == null){
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
    ObjectMapper mapper = new ObjectMapper();
    List<String> updateConditionDataList = new ArrayList<>();
    try {
      for (JsonNode jsonNode : mapper.readTree(message)) {
        updateConditionDataList.add(jsonNode.asText());
      }
      return Response.accepted(localOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName)).build();
    } catch (JsonProcessingException e) {
      LOGGER.error("Badly formed Json request body {}", message, e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    } catch (IOException e) {
      LOGGER.error("An error occurred processing the PUT request for ogel with ID {}", ogelId, e);
      throw new RuntimeException("An error occurred updating the Ogel.", e);
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred processing updating the ogel with ID {}", ogelId, e);
      throw new RuntimeException("Update Request Unsuccessful " + e);
    }
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOrUpdateOgel(@Auth PrincipalImpl user,
                                     @NotNull @PathParam("id") String ogelId,
                                     String message) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      LocalOgel localOgel = objectMapper.readValue(message, LocalOgel.class);
      if(localOgel.getName() == null && localOgel.getSummary() == null){
        return Response.status(BAD_REQUEST.getStatusCode()).entity("Invalid or empty property name found in the request json").build();
      }
      ogelService.findSpireOgelById(ogelId);

      localOgel.setId(ogelId);
      localOgelService.insertOrUpdateOgel(localOgel);
      return Response.status(Response.Status.CREATED).entity(localOgel).type(MediaType.APPLICATION_JSON).build();
    } catch (OgelNotFoundException e) {
      LOGGER.error("There is no ogel found with ID {}", ogelId);
      return Response.status(INTERNAL_SERVER_ERROR.getStatusCode()).entity(new ErrorMessage(e.getMessage())).build();
    } catch (IOException e) {
      LOGGER.error("An error occurred converting request body json to an object", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred processing handling the insert new or update ogel request with ID {}", ogelId, e);
      throw new RuntimeException("Request Unsuccessful " + e);
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOgelArray(@Auth PrincipalImpl user, String message) {
    ObjectMapper jsonMapper = new ObjectMapper();
    try {
      final List<LocalOgel> ogelList = jsonMapper.readValue(message,
          jsonMapper.getTypeFactory().constructCollectionType(List.class, LocalOgel.class));
      localOgelService.insertOgelList(ogelList);
    } catch (JsonParseException e) {
      LOGGER.error("An error occurred parsing the json request body", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred deserializing the json", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, e.getMessage())).build();
    } catch (IOException e) {
      LOGGER.error("Unexpected error occurred parsing the json", e);
      throw new RuntimeException("An error occurred reading/parsing the message body json.", e);
    } catch (SQLException e) {
      LOGGER.error("An error occurred persisting new local ogels data into database", e);
      throw new RuntimeException("Database error", e);
    }
    return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).build();
  }
}
