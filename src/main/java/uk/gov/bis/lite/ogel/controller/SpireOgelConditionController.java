package uk.gov.bis.lite.ogel.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;
import uk.gov.bis.lite.ogel.service.LocalSpireOgelService;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

@Path("/ogel")
@Produces(MediaType.APPLICATION_JSON)
public class SpireOgelConditionController {

  private final SpireOgelService ogelService;
  private final LocalSpireOgelService localSpireOgelService;

  @Inject
  public SpireOgelConditionController(SpireOgelService ogelService, LocalSpireOgelService localOgelService) {
    this.ogelService = ogelService;
    this.localSpireOgelService = localOgelService;
  }

  @GET
  @Path("{id}")
  public Response getOgelByOgelID(@NotNull @PathParam("id") String ogelId)
      throws SOAPException, XPathExpressionException, UnsupportedEncodingException {
    final List<LocalSpireOgel> allLocalOgels = (List<LocalSpireOgel>) localSpireOgelService.getAllLocalOgels();
    List<SpireOgel> ogelList = ogelService.getAllOgels();

    ogelList.stream().
        forEach(o -> o.setLocalSpireOgel(getMatchingLocalSpireOgel(allLocalOgels, o.getId()).orElse(null)));

    final Optional<SpireOgel> matchingSpireOgel = ogelList.stream().filter(o -> o.getId().equalsIgnoreCase(ogelId)).findAny();
    if (matchingSpireOgel.isPresent()) {
      OutboundMessageContext messageContext = new OutboundMessageContext();
      messageContext.setMediaType(MediaType.APPLICATION_JSON_TYPE);
      messageContext.setEntity(matchingSpireOgel.get());
      return new OutboundJaxrsResponse(Response.Status.OK, messageContext);
    } else {
      return Response.status(404).entity("Given Ogel ID is not found").type(MediaType.APPLICATION_JSON_TYPE).build();
    }
  }

  private Optional<LocalSpireOgel> getMatchingLocalSpireOgel(List<LocalSpireOgel> allLocalOgels, String ogelID) {
    return allLocalOgels.stream().filter(lo -> ogelID.equalsIgnoreCase(lo.getId())).findAny();
  }

  @PUT
  @Path("{id}/summary-data/{conditionFieldName}")
  @Consumes(MediaType.APPLICATION_JSON)
  public LocalSpireOgel updateOgelCondition(@NotNull @PathParam("id") String ogelId,
                                            @NotNull @PathParam("conditionFieldName") String conditionFieldName, String message) {
    ObjectMapper mapper = new ObjectMapper();
    List<String> updateConditionDataList = new ArrayList<>();
    try {
      final Iterator<JsonNode> jsonConditionArrayIterator = mapper.readTree(message).iterator();
      while (jsonConditionArrayIterator.hasNext()) {
        updateConditionDataList.add(jsonConditionArrayIterator.next().asText());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return localSpireOgelService.updateSpireOgelCondition(ogelId, updateConditionDataList, conditionFieldName);
  }
}
