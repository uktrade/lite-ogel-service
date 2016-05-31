package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
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

@Path("/ogel")
@Produces(MediaType.APPLICATION_JSON)
public class SpireMergedOgelViewResource {

  private final SpireOgelService ogelService;
  private final LocalOgelService localOgelService;

  @Inject
  public SpireMergedOgelViewResource(SpireOgelService ogelService, LocalOgelService localOgelService) {
    this.ogelService = ogelService;
    this.localOgelService = localOgelService;
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public OgelFullView getOgelByOgelID(@NotNull @PathParam("id") String ogelId)
      throws OgelNotFoundException, LocalOgelNotFoundException, SOAPParseException {
    List<SpireOgel> ogelList = ogelService.getAllOgels();
    final SpireOgel foundSpireOgel = ogelService.findSpireOgelById(ogelList, ogelId);
    if (foundSpireOgel == null) {
      throw new OgelNotFoundException(ogelId);
    }
    LocalOgel localOgelFound = localOgelService.findLocalOgelById(ogelId);
    if (localOgelFound == null) {
      throw new LocalOgelNotFoundException(ogelId);
    }
    OgelFullView ogelFullView = new OgelFullView();
    ogelFullView.setLocalOgel(localOgelFound);
    ogelFullView.setSpireOgel(foundSpireOgel);
    return ogelFullView;
  }

  @PUT
  @Path("{id}/summary-data/{conditionFieldName}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateOgelCondition(@NotNull @PathParam("id") String ogelId,
                                      @NotNull @PathParam("conditionFieldName") String conditionFieldName, String message) throws
      IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<String> updateConditionDataList = new ArrayList<>();
    try {
      final Iterator<JsonNode> jsonConditionArrayIterator = mapper.readTree(message).iterator();
      while (jsonConditionArrayIterator.hasNext()) {
        updateConditionDataList.add(jsonConditionArrayIterator.next().asText());
      }
    } catch (IOException e) {
      if (e instanceof JsonProcessingException) {
        return Response.status(BAD_REQUEST.getStatusCode()).build();
      }
      throw e;
    }
    try {
      return Response.accepted(localOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName)).build();
    } catch (Exception e) {
      return Response.notModified("Update Request Unsuccessful " + e.getMessage()).build();
    }
  }
}
