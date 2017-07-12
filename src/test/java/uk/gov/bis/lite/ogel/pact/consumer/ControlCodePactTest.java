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

  private static final List<String> CONTROL_CODES = Arrays.asList("C1","C2");

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void before() {
    controlCodeClient = new ControlCodeClient(ClientBuilder.newClient(), mockProvider.getConfig().url());
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getAllControlCodesSuccess(PactDslWithProvider builder) {

    return builder
        .given("control codes exist")
        .uponReceiving("request to get all control codes")
        .path("/control-codes")
        .method("GET")
        .willRespondWith()
          .status(200)
          .headers(headers())
          .body(controlCodeFullViewPactDsl())
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getBulkCCAllMatch(PactDslWithProvider builder) {

    return builder
        .given("control codes exist")
        .uponReceiving("request to get bulk control codes")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=C1&controlCode=C2")
        .willRespondWith()
          .status(200)
          .headers(headers())
          .body(bulkControlCodeAllMatchResponsePactDsl())
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getBulkCCMatchAndNoMatchSuccess(PactDslWithProvider builder) {

    return builder
        .given("some control codes exist")
        .uponReceiving("request to get bulk control codes")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=EXISTING_CODE&controlCode=MISSING_CODE")
        .willRespondWith()
          .status(206)
          .headers(headers())
          .body(bulkCCMatchAndNoMatchResponsePactDsl())
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getBulkCCNoneMatch(PactDslWithProvider builder) {

    return builder
        .given("control codes do not exist")
        .uponReceiving("request to get bulk control codes")
        .path("/bulk-control-codes")
        .method("GET")
        .query("controlCode=C1&controlCode=C2")
        .willRespondWith()
          .status(206)
          .headers(headers())
          .body(getBulkCCNoneMatchResponsePactDsl())
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getAllControlCodesSuccess")
  public void shouldGetAllControlCodes() throws Exception {
    List<ControlCodeFullView> result = controlCodeClient.getAllControlCodes();
    assertThat(controlCodeClient.getAllControlCodes()).isNotEmpty();
    assertThat(result.get(0).getControlCode()).isEqualTo("C1");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getBulkCCAllMatch")
  public void shouldGetBulkCCAllMatch() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(CONTROL_CODES);
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews().get(0).getControlCode()).isEqualTo("C1");
    assertThat(bulkControlCodes.getControlCodeFullViews().get(0).getId()).isEqualTo("1");
    assertThat(bulkControlCodes.getControlCodeFullViews().get(0).getFriendlyDescription()).isEqualTo("Friendly Description");
    assertThat(bulkControlCodes.getMissingControlCodes().isEmpty());
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getBulkCCMatchAndNoMatchSuccess")
  public void shouldGetBulkCCMatchAndNoMatch() throws Exception {
    BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(Arrays.asList("EXISTING_CODE","MISSING_CODE"));
    assertThat(bulkControlCodes).isNotNull();
    assertThat(bulkControlCodes.getControlCodeFullViews()).extracting(e -> e.getControlCode()).containsOnly("EXISTING_CODE");
    assertThat(bulkControlCodes.getControlCodeFullViews().get(0).getId()).isEqualTo("1");
    assertThat(bulkControlCodes.getControlCodeFullViews().get(0).getFriendlyDescription()).isEqualTo("Friendly Description");
    assertThat(bulkControlCodes.getMissingControlCodes().get(0)).isEqualTo("MISSING_CODE");
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
        .stringType("controlCode","C1")
        .closeObject();
  }

  private DslPart bulkControlCodeAllMatchResponsePactDsl() {
    return new PactDslJsonBody()
        .minArrayLike("controlCodeFullViews", 1)
          .stringType("id","1")
          .stringType("controlCode","C1")
          .stringType("friendlyDescription","Friendly Description")
        .closeObject()
        .closeArray()
        .asBody()
        .minArrayLike("missingControlCodes", 0)
        .asBody();
  }

  private DslPart bulkCCMatchAndNoMatchResponsePactDsl() {
    return new PactDslJsonBody()
        .minArrayLike("controlCodeFullViews", 1)
          .stringType("id","1")
          .stringType("controlCode","EXISTING_CODE")
          .stringType("friendlyDescription","Friendly Description")
        .closeObject()
        .closeArray()
        .asBody()
        .minArrayLike("missingControlCodes", 1, PactDslJsonRootValue.stringType("MISSING_CODE"), 1)
        .asBody();
  }

  private DslPart getBulkCCNoneMatchResponsePactDsl() {
    return new PactDslJsonBody()
        .minArrayLike("controlCodeFullViews", 0)
          .closeObject()
          .closeArray()
        .asBody()
        .minArrayLike("missingControlCodes", 1, PactDslJsonRootValue.stringType("C1"), 1)
        .asBody();
  }

  Map<String, String> headers() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    return headers;
  }
}
