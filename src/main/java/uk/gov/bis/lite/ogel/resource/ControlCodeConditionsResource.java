package uk.gov.bis.lite.ogel.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.exception.SOAPParseException;
import uk.gov.bis.lite.ogel.model.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.model.ControlCodeCutDown;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.validator.CheckLocalControlCodeConditionList;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
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

@Path("/control-code-conditions")
@Produces(MediaType.APPLICATION_JSON)
public class ControlCodeConditionsResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControlCodeConditionsResource.class);

  private final SpireOgelService ogelService;
  private final LocalOgelService localOgelService;
  private final LocalControlCodeConditionService localControlCodeConditionService;
  private final String controlCodeServiceBulkGetUrl;
  private final HttpClient httpClient;

  @Inject
  public ControlCodeConditionsResource(SpireOgelService ogelService, LocalOgelService localOgelService,
                                       LocalControlCodeConditionService localControlCodeConditionService,
                                       HttpClient httpClient,
                                       @Named("controlCodeServiceBulkGetUrl") String controlCodeServiceBulkGetUrl) {
    this.ogelService = ogelService;
    this.localOgelService = localOgelService;
    this.localControlCodeConditionService = localControlCodeConditionService;
    this.controlCodeServiceBulkGetUrl = controlCodeServiceBulkGetUrl;
    this.httpClient = httpClient;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<LocalControlCodeCondition> getAllControlCodeConditions()
      throws OgelNotFoundException, SOAPParseException {
    return localControlCodeConditionService.getAllControlCodeConditions();
  }

  @GET
  @Path("{ogelID}/{controlCode}")
  @Produces(MediaType.APPLICATION_JSON)
  public ControlCodeConditionFullView getOgelByOgelID(@NotNull @PathParam("ogelID") String ogelID,
                                                      @NotNull @PathParam("controlCode") String controlCode) {
    LocalOgel localOgelFound = localOgelService.findLocalOgelById(ogelID);
    if (localOgelFound == null) {
      LOGGER.warn("Local OGEL Not Found for OGEL ID: {}", ogelID);
    }

    // TODO - Control code validation?

    LocalControlCodeCondition localControlCodeConditions = localControlCodeConditionService.getLocalControlCodeConditionsByIdAndControlCode(ogelID, controlCode);

    List<ControlCodeCutDown> controlCodeCutDownList;
    if (localControlCodeConditions.getConditionDescriptionControlCodes().size() > 0) {
      HttpPost postRequest = new HttpPost(controlCodeServiceBulkGetUrl);
      final StringWriter sw = new StringWriter();
      final ObjectMapper mapper = new ObjectMapper();
      try {
        mapper.writeValue(sw, localControlCodeConditions.getConditionDescriptionControlCodes());
        StringEntity input = new StringEntity(sw.toString(), ContentType.APPLICATION_JSON);
        sw.close();
        postRequest.setEntity(input);

        HttpResponse response = httpClient.execute(postRequest);

        controlCodeCutDownList = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<ControlCodeCutDown>>() {});

        return new ControlCodeConditionFullView(localControlCodeConditions, controlCodeCutDownList);
      } catch (IOException e) {
        // TODO - How to handle errors here nicely?
        throw new RuntimeException("Failed getting to the control code service", e);
      }
    }
    else {
      return new ControlCodeConditionFullView(localControlCodeConditions, Collections.emptyList());
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertOgelConditionsArray(@Auth PrincipalImpl user, @CheckLocalControlCodeConditionList List<LocalControlCodeCondition> ogelConditionsList)
  throws OgelNotFoundException {
    if (ogelConditionsList.isEmpty()) {
      return Response.status(BAD_REQUEST.getStatusCode()).entity(new ErrorMessage(400, "Empty OGEL Conditions List")).build();
    }

    // Check OGEL IDs exist on SPIRE too or throw OgelNotFoundException
    ogelConditionsList.forEach(o -> ogelService.findSpireOgelById(o.getOgelID()));

    localControlCodeConditionService.insertControlCodeConditionList(ogelConditionsList);

    List<String> insertedOgelIDs = ogelConditionsList.stream().map(LocalControlCodeCondition::getOgelID).collect(Collectors.toList());
    return Response.status(Response.Status.CREATED).entity(
        getAllControlCodeConditions().stream().filter(ccc -> insertedOgelIDs.contains(ccc.getOgelID())).collect(Collectors.toList()))
        .type(MediaType.APPLICATION_JSON).build();
  }
}
