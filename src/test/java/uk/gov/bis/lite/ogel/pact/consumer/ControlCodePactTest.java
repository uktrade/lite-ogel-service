package uk.gov.bis.lite.ogel.pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.client.ControlCodeClient;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;

public class ControlCodePactTest {

  private ControlCodeClient controlCodeClient;

  private final static String PROVIDER = "lite-control-code-service";
  private final static String CONSUMER = "lite-ogel-service";

  private final static List<String> CONTROL_CODES = Arrays.asList("C1", "C2");

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void before() {
    controlCodeClient = new ControlCodeClient(ClientBuilder.newClient(), mockProvider.getConfig().url());
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getAllControlCodesSuccess(PactDslWithProvider builder) {

    return builder
        .uponReceiving("request to get all control codes")
        .path("/control-codes")
        .method("GET")
        .willRespondWith()
          .status(200)
          .body(controlCodeFullViewPactDsl())
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getBulkControlCodesSuccess(PactDslWithProvider builder) {

    return builder
        .uponReceiving("request to get bulk control codes")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=C1&controlCode=C2")
        .willRespondWith()
          .status(200)
          .body(bulkControlCodeResponsePactDsl())
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getAllControlCodesSuccess")
  public void shouldGetAllControlCodes() throws Exception {
    List<ControlCodeFullView> result = controlCodeClient.getAllControlCodes();
    assertThat(controlCodeClient.getAllControlCodes()).isNotEmpty();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getBulkControlCodesSuccess")
  public void shouldGetBulkControlCodes() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(CONTROL_CODES);
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews()).extracting(e -> e.getControlCode()).containsOnly("C1");
    assertThat(bulkControlCodes.getMissingControlCodes().get(0)).contains("C2");
  }

  private DslPart controlCodeFullViewPactDsl() {
      return PactDslJsonArray.arrayMinLike(1)
          .stringType("id")
          .stringType("parentId")
          .stringType("controlCode")
          .stringType("title")
          .stringType("technicalNotes")
          .stringType("alias")
          .stringType("category")
          .stringType("friendlyDescription")
          .stringType("legalDescription")
          .minArrayLike("decontrols", 0, PactDslJsonRootValue.stringType())
          .object("additionalSpecifications")
            .stringType("clauseText")
            .minArrayLike("specificationText", 0, PactDslJsonRootValue.stringType())
            .minArrayLike("specificationControlCodes", 0, PactDslJsonRootValue.stringType())
          .closeObject().asBody()
          .booleanType("selectable")
          .booleanType("showInHierarchy")
          .stringType("revisionDate")
          .stringType("lastModifiedInRevision")
          .stringType("beforeLegalDefinitionText")
          .stringType("afterLegalDefinitionText")
          .stringType("displayOrder")
          .stringType("reasonForControl")
          .stringType("definitionOfTerms")
        .closeObject();
  }


  private DslPart bulkControlCodeResponsePactDsl() {
    return new PactDslJsonBody()
        .minArrayLike("controlCodeFullViews", 1)
        .stringType("id")
        .stringType("parentId")
        .stringType("controlCode","C1")
        .stringType("title")
        .stringType("technicalNotes")
        .stringType("alias")
        .stringType("category")
        .stringType("friendlyDescription")
        .stringType("legalDescription")
        .minArrayLike("decontrols", 0, PactDslJsonRootValue.stringType())
        .object("additionalSpecifications")
        .stringType("clauseText")
        .minArrayLike("specificationText", 0, PactDslJsonRootValue.stringType())
        .minArrayLike("specificationControlCodes", 0, PactDslJsonRootValue.stringType())
        .closeObject().asBody()
        .booleanType("selectable")
        .booleanType("showInHierarchy")
        .stringType("revisionDate")
        .stringType("lastModifiedInRevision")
        .stringType("beforeLegalDefinitionText")
        .stringType("afterLegalDefinitionText")
        .stringType("displayOrder")
        .stringType("reasonForControl")
        .stringType("definitionOfTerms")
        .closeObject()
        .closeArray().asBody()
        .minArrayLike("missingControlCodes", 1, PactDslJsonRootValue.stringType("C2"), 1)
        .asBody();
  }

//  private DslPart bulkControlCodeNoneMatchResponsePactDsl() {
//    return new PactDslJsonBody()
//        .maxArrayLike("controlCodeFullViews", 0)
//        .stringType("id")
//        .stringType("parentId")
//        .stringType("controlCode","C1")
//        .stringType("title")
//        .stringType("technicalNotes")
//        .stringType("alias")
//        .stringType("category")
//        .stringType("friendlyDescription")
//        .stringType("legalDescription")
//        .minArrayLike("decontrols", 0, PactDslJsonRootValue.stringType())
//        .object("additionalSpecifications")
//        .stringType("clauseText")
//        .minArrayLike("specificationText", 0, PactDslJsonRootValue.stringType())
//        .minArrayLike("specificationControlCodes", 0, PactDslJsonRootValue.stringType())
//        .closeObject().asBody()
//        .booleanType("selectable")
//        .booleanType("showInHierarchy")
//        .stringType("revisionDate")
//        .stringType("lastModifiedInRevision")
//        .stringType("beforeLegalDefinitionText")
//        .stringType("afterLegalDefinitionText")
//        .stringType("displayOrder")
//        .stringType("reasonForControl")
//        .stringType("definitionOfTerms")
//        .closeObject()
//        .closeArray().asBody()
//        .minArrayLike("missingControlCodes", 1, PactDslJsonRootValue.stringType("C2"), 1)
//        .asBody();
//  }
//
//  private DslPart bulkControlCodeAllMatchResponsePactDsl() {
//    return new PactDslJsonBody()
//        .minArrayLike("controlCodeFullViews", 1)
//        .stringType("id")
//        .stringType("parentId")
//        .stringType("controlCode","C1")
//        .stringType("title")
//        .stringType("technicalNotes")
//        .stringType("alias")
//        .stringType("category")
//        .stringType("friendlyDescription")
//        .stringType("legalDescription")
//        .minArrayLike("decontrols", 0, PactDslJsonRootValue.stringType())
//        .object("additionalSpecifications")
//        .stringType("clauseText")
//        .minArrayLike("specificationText", 0, PactDslJsonRootValue.stringType())
//        .minArrayLike("specificationControlCodes", 0, PactDslJsonRootValue.stringType())
//        .closeObject().asBody()
//        .booleanType("selectable")
//        .booleanType("showInHierarchy")
//        .stringType("revisionDate")
//        .stringType("lastModifiedInRevision")
//        .stringType("beforeLegalDefinitionText")
//        .stringType("afterLegalDefinitionText")
//        .stringType("displayOrder")
//        .stringType("reasonForControl")
//        .stringType("definitionOfTerms")
//        .closeObject()
//        .closeArray().asBody()
//        .maxArrayLike("missingControlCodes", 0)
//        .asBody();
//  }

  //  Map<String, String> headers() {
//    Map<String, String> headers = new HashMap<>();
//    headers.put("Content-Type", "application/json");
//    return headers;
//  }
}
