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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;

public class ControlCodePactTest {

  private ControlCodeClient controlCodeClient;

  private static final String PROVIDER = "lite-control-code-service";
  private static final String CONSUMER = "lite-ogel-service";

  private static final List<String> CONTROL_CODES = Arrays.asList("C1");

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
  public PactFragment getBulkCCAllMatch(PactDslWithProvider builder) {

    return builder
        .uponReceiving("request to get bulk control codes - all match")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=C1")
        .willRespondWith()
          .status(200)
          .body(bulkControlCodeAllMatchResponsePactDsl())
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getBulkCCMatchAndNoMatchSuccess(PactDslWithProvider builder) {

    return builder
        .uponReceiving("request to get bulk control codes - match and no match")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=C1")
        .willRespondWith()
          .status(200)
          .body(bulkCCMatchAndNoMatchResponsePactDsl())
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getBulkCCNoneMatch(PactDslWithProvider builder) {

    return builder
        .uponReceiving("request to get bulk control codes - none match")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=C1")
        .willRespondWith()
        .status(206)
        .body(getBulkCCNoneMatchResponsePactDsl())
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getAllControlCodesSuccess")
  public void shouldGetAllControlCodes() throws Exception {
    List<ControlCodeFullView> result = controlCodeClient.getAllControlCodes();
    assertThat(controlCodeClient.getAllControlCodes()).isNotEmpty();
    assertThat(result.get(0).getControlCode()).isEqualTo("C1");
    assertThat(result.get(0).getId()).isEqualTo("1");
    assertThat(result.get(0).getLegalDescription()).isEqualTo("Control Code test");
    assertThat(result.get(0).getCategory()).isEqualTo("RATING");
    assertThat(result.get(0).isSelectable()).isEqualTo(true);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getBulkCCAllMatch")
  public void shouldGetBulkCCAllMatch() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(CONTROL_CODES);
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews().get(0).getControlCode()).isEqualTo("C1");
    assertThat(bulkControlCodes.getMissingControlCodes().isEmpty());
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getBulkCCMatchAndNoMatchSuccess")
  public void shouldGetBulkCCMatchAndNoMatch() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(CONTROL_CODES);
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews()).extracting(e -> e.getControlCode()).containsOnly("C1");
    assertThat(bulkControlCodes.getMissingControlCodes().get(0)).isEqualTo("C2");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getBulkCCNoneMatch")
  public void shouldGetBulkCCNoneMatch() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(CONTROL_CODES);
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews()).extracting(e -> e.getControlCode()).isEmpty();
    assertThat(bulkControlCodes.getMissingControlCodes().get(0)).isEqualTo("C1");
  }

  private DslPart controlCodeFullViewPactDsl() {
      return PactDslJsonArray.arrayMinLike(1)
          .stringType("id","1")
          .stringType("parentId")
          .stringType("controlCode","C1")
          .stringType("title")
          .stringType("technicalNotes")
          .stringType("alias")
          .stringType("category", "RATING")
          .stringType("friendlyDescription")
          .stringType("legalDescription", "Control Code test")
          .minArrayLike("decontrols", 0).closeObject().closeArray()
          .object("additionalSpecifications")
            .stringType("clauseText")
            .minArrayLike("specificationText", 0, PactDslJsonRootValue.stringType())
            .minArrayLike("specificationControlCodes", 0, PactDslJsonRootValue.stringType())
          .closeObject().asBody()
          .booleanType("selectable", true)
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

  private DslPart bulkCCMatchAndNoMatchResponsePactDsl() {
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

  private DslPart getBulkCCNoneMatchResponsePactDsl() {
    return new PactDslJsonBody()
        .minArrayLike("controlCodeFullViews", 0)
          .closeObject()
          .closeArray()
        .asBody()
        .minArrayLike("missingControlCodes", 0, PactDslJsonRootValue.stringType("C1"),1)
        .asBody();
  }

  private DslPart bulkControlCodeAllMatchResponsePactDsl() {
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
        .closeArray()
        .asBody()
        .minArrayLike("missingControlCodes", 0)
        .closeObject()
        .closeArray()
        .asBody();
  }

}
