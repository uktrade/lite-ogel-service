package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundException;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.database.exception.SOAPParseException;
import uk.gov.bis.lite.ogel.model.OgelFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public OgelFullView getOgelByOgelID(@NotNull @PathParam("id") String ogelId)
      throws OgelNotFoundException, LocalOgelNotFoundException, SOAPParseException {
    final SpireOgel foundSpireOgel = ogelService.findSpireOgelById(ogelId);
    LocalOgel localOgelFound = localOgelService.findLocalOgelById(ogelId);
    return new OgelFullView(foundSpireOgel, localOgelFound);
  }

  @PUT
  @Path("{id}/summary-data/{conditionFieldName}")
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
      final Iterator<JsonNode> jsonConditionArrayIterator = mapper.readTree(message).iterator();
      while (jsonConditionArrayIterator.hasNext()) {
        updateConditionDataList.add(jsonConditionArrayIterator.next().asText());
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("Badly formed Json request body {}", message, e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
    } catch (IOException e) {
      LOGGER.error("An error occurred processing the PUT request for ogel with ID {}", ogelId, e);
      throw new RuntimeException("An error occurred updating the Ogel.", e);
    }
    try {
      return Response.accepted(localOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName)).build();
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
      ogelService.findSpireOgelById(ogelId);

      localOgelService.insertOrUpdateOgel(localOgel);
      return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).build();
    } catch (OgelNotFoundException e) {
      LOGGER.error("There is no ogel found with ID {}", ogelId);
      return Response.status(NOT_FOUND.getStatusCode()).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    } catch (IOException e) {
      LOGGER.error("An error occurred converting request body json to an object", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred processing handling the insert new or update ogel request with ID {}", ogelId, e);
      throw new RuntimeException("Request Unsuccessful " + e);
    }
  }

  @PUT
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOgelArray(@Auth PrincipalImpl user, String message) {
    ObjectMapper jsonMapper = new ObjectMapper();
    try {
      final List<LocalOgel> ogelList = jsonMapper.readValue(message,
          jsonMapper.getTypeFactory().constructCollectionType(List.class, LocalOgel.class));
      ogelList.stream().forEach(localOgelService::insertOrUpdateOgel);
    } catch (JsonParseException e) {
      LOGGER.error("An error occurred parsing the json request body", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred deserializing the json", e);
      return Response.status(BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
    } catch (IOException e) {
      LOGGER.error("Unexpected error occurred parsing the json", e);
      throw new RuntimeException("An error occurred reading/parsing the message body json.", e);
    }
    return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).build();
  }
}
