package uk.gov.bis.lite.ogel.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import uk.gov.bis.lite.ogel.model.OgelFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

@Path("/ogel")
@Produces(MediaType.APPLICATION_JSON)
public class SpireOgelConditionResource {

  private final SpireOgelService ogelService;
  private final LocalOgelService localOgelService;

  @Inject
  public SpireOgelConditionResource(SpireOgelService ogelService, LocalOgelService localOgelService) {
    this.ogelService = ogelService;
    this.localOgelService = localOgelService;
  }

  @GET
  @Path("{id}")
  public OgelFullView getOgelByOgelID(@NotNull @PathParam("id") String ogelId)
      throws SOAPException, XPathExpressionException, UnsupportedEncodingException {
    final List<LocalOgel> allLocalOgels = (List<LocalOgel>) localOgelService.getAllLocalOgels();
    List<SpireOgel> ogelList = ogelService.getAllOgels();

    List<OgelFullView> viewList = new ArrayList<>();

    ogelList.stream().
        forEach(o -> viewList.add( new OgelFullView(o, (getMatchingLocalSpireOgel(allLocalOgels, o.getId()).orElse(null)))));

    final Optional<OgelFullView> matchingSpireOgel = viewList.stream().filter(v -> v.getSpireOgel().getId().equalsIgnoreCase(ogelId)).findAny();
    if (matchingSpireOgel.isPresent()) {
      return matchingSpireOgel.get();
    } else {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  private Optional<LocalOgel> getMatchingLocalSpireOgel(List<LocalOgel> allLocalOgels, String ogelID) {
    return allLocalOgels.stream().filter(lo -> ogelID.equalsIgnoreCase(lo.getId())).findAny();
  }

  @PUT
  @Path("{id}/summary-data/{conditionFieldName}")
  @Consumes(MediaType.APPLICATION_JSON)
  public LocalOgel updateOgelCondition(@NotNull @PathParam("id") String ogelId,
                                       @NotNull @PathParam("conditionFieldName") String conditionFieldName, String message) {
    ObjectMapper mapper = new ObjectMapper();
    List<String> updateConditionDataList = new ArrayList<>();
    try {
      final Iterator<JsonNode> jsonConditionArrayIterator = mapper.readTree(message).iterator();
      while (jsonConditionArrayIterator.hasNext()) {
        updateConditionDataList.add(jsonConditionArrayIterator.next().asText());
      }
    } catch (IOException e) {
      Response.serverError().
          entity("An unknown error occurred while updating the ogel condition list for ogel " + ogelId + "\n" + e.getMessage()).build();
    }
    return localOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName);
  }
}
