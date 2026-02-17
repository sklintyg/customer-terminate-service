package se.inera.intyg.cts.integrationtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.restassured.RestAssured;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.testability.dto.TestabilityExportEmbeddableDTO;

class ReceiptIT {

  private TestData testData;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = System.getProperty("integration.tests.baseUrl",
        "http://cts.localtest.me");
    testData = TestData.create();
  }

  @AfterEach
  void tearDown() {
    testData.cleanUp();
    RestAssured.reset();
  }

  @Test
  void shallUpdateStatusWhenReceiptReceived() {
    testData.defaultTermination().setup();

    final var terminationId = testData.terminationIds().get(0);

    given()
        .pathParam("terminationId", terminationId)
        .when().post("/api/v1/receipt/{terminationId}")
        .then().statusCode(HttpStatus.OK.value());

    final var terminationDTO = given()
        .pathParam("terminationId", terminationId)
        .when().get("/api/v1/terminations/{terminationId}")
        .then().statusCode(HttpStatus.OK.value())
        .extract().response().as(TerminationDTO.class);

    assertEquals(TerminationStatus.RECEIPT_RECEIVED.description(), terminationDTO.status());
  }

  @Test
  void shallSetReceiptTime() {
    testData.defaultTermination().setup();

    final var terminationId = testData.terminationIds().get(0);

    given()
        .pathParam("terminationId", terminationId)
        .when().post("/api/v1/receipt/{terminationId}")
        .then().statusCode(HttpStatus.OK.value());

    final var exportEmbeddableDTO = given()
        .pathParam("terminationId", terminationId)
        .when().get("/testability/v1/terminations/export/{terminationId}")
        .then().statusCode(HttpStatus.OK.value())
        .extract().response().as(TestabilityExportEmbeddableDTO.class);

    assertNotNull(exportEmbeddableDTO.receiptTime());
  }

  @Test
  void shallReturnNotFoundIfReceiptForNonExistingTermination() {
    testData.defaultTermination().setup();

    given()
        .pathParam("terminationId", UUID.randomUUID())
        .when().post("/api/v1/receipt/{terminationId}")
        .then().statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void shallSendPasswordToOrganizationRepresentativeWhenReceiptIsReceived() {
    testData
        .defaultTermination()
        .certificates(50)
        .collectCertificates()
        .certificateTexts(10)
        .collectCertificateTexts()
        .uploadPackage("password")
        .receiptReceived()
        .setup();

    given()
        .when()
        .post("/api/v1/exports/sendPasswords")
        .then()
        .statusCode(HttpStatus.OK.value());

    final var generatedPassword = getPassword(testData.terminationIds().get(0));
    final var passwordSentBySMS = getPasswordSentBySMS();

    assertEquals(generatedPassword, passwordSentBySMS);
  }

  private String getPassword(String terminationId) {
    return given()
        .pathParam("terminationId", terminationId)
        .when()
        .get("/testability/v1/terminations/{terminationId}/password")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract().asString();
  }

  private String getPasswordSentBySMS() {
    return given()
        .baseUri("http://localhost:18000")
        .pathParam("phoneNumber", TestData.PHONE_NUMBER)
        .when()
        .get("/testability-tellustalk/v1/passwords/{phoneNumber}")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .asString();
  }
}
