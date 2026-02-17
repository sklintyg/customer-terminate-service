package se.inera.intyg.cts.integrationtest;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import se.inera.intyg.cts.testability.dto.TestabilityTerminationDTO;

public class TestData {

  public static final String HSA_ID = "HSA-ID";
  public static final String ORG_NO = "ORG-NO";
  public static final String PERSON_ID = "191212121212";
  public static final String PHONE_NUMBER = "070-1112233";
  public static final String EMAIL_ADDRESS = "email@address.se";

  private final List<TestabilityTerminationDTO> terminationDTOs = new ArrayList<>();
  private final List<String> terminationIds = new ArrayList<>();

  private int certificatesCount;
  private int certificateTextsCount;
  private boolean collectCertificates;
  private boolean uploadPackage;
  private String password;
  private boolean receiptReceived;
  private boolean notificationSent;
  private LocalDateTime notificationSentTime;

  public static TestData create() {
    return new TestData();
  }

  private TestData() {
  }

  public TestData defaultTermination() {
    defaultTerminations(1);
    return this;
  }

  public TestData defaultTerminations(int count) {
    for (int i = 0; i < count; i++) {
      terminationDTOs.add(
          new TestabilityTerminationDTO(
              UUID.randomUUID(),
              LocalDateTime.now(),
              "CREATORHSA-ID",
              "Creator Name",
              "CREATED",
              HSA_ID,
              ORG_NO,
              PERSON_ID,
              PHONE_NUMBER,
              EMAIL_ADDRESS
          )
      );
    }
    return this;
  }

  public TestData certificates(int count) {
    certificatesCount = count;
    return this;
  }

  public TestData certificateTexts(int count) {
    certificateTextsCount = count;
    return this;
  }

  public TestData collectCertificates() {
    this.collectCertificates = true;
    return this;
  }

  public TestData collectCertificateTexts() {
    this.collectCertificates = true;
    return this;
  }

  public TestData uploadPackage(String password) {
    this.uploadPackage = true;
    this.password = password;
    return this;
  }

  public TestData notificationSent(LocalDateTime notificationSentTime) {
    this.notificationSent = true;
    this.notificationSentTime = notificationSentTime;
    return this;
  }

  public TestData receiptReceived() {
    this.receiptReceived = true;
    return this;
  }

  public TestData setup() {
    if (!terminationDTOs.isEmpty()) {
      for (TestabilityTerminationDTO testabilityTerminationDTO : terminationDTOs) {
        given()
            .contentType(ContentType.JSON)
            .body(testabilityTerminationDTO)
            .when()
            .post("/testability/v1/terminations")
            .then()
            .statusCode(HttpStatus.OK.value());

        terminationIds.add(testabilityTerminationDTO.terminationId().toString());

        if (collectCertificates) {
          given()
              .contentType(ContentType.JSON)
              .body(certificates())
              .pathParam("terminationId", testabilityTerminationDTO.terminationId().toString())
              .when()
              .post("/testability/v1/terminations/{terminationId}/certificates")
              .then()
              .statusCode(HttpStatus.OK.value());
        } else {
          given()
              .baseUri("http://localhost:18000")
              .contentType(ContentType.JSON)
              .body(new IntygstjanstCertificatesDTO(certificates()))
              .pathParam("careProvider", testabilityTerminationDTO.hsaId())
              .when()
              .post("/testability-intygstjanst/v1/certificates/{careProvider}")
              .then()
              .statusCode(HttpStatus.OK.value());
        }

        if (collectCertificates) {
          given()
              .contentType(ContentType.JSON)
              .body(certificateTexts())
              .pathParam("terminationId", testabilityTerminationDTO.terminationId().toString())
              .when()
              .post("/testability/v1/terminations/{terminationId}/certificatetexts")
              .then()
              .statusCode(HttpStatus.OK.value());
        } else if (certificateTextsCount > 0) {
          given()
              .baseUri("http://localhost:18000")
              .contentType(ContentType.JSON)
              .body(certificateTexts())
              .when()
              .post("/testability-intygstjanst/v1/certificatetexts")
              .then()
              .statusCode(HttpStatus.OK.value());
        }

        if (uploadPackage) {
          given()
              .contentType(ContentType.JSON)
              .body(password)
              .pathParam("terminationId", testabilityTerminationDTO.terminationId().toString())
              .when()
              .post("/testability/v1/terminations/{terminationId}/upload")
              .then()
              .statusCode(HttpStatus.OK.value());
        }

        if (notificationSent) {
          given()
              .contentType(ContentType.JSON)
              .body(notificationSentTime)
              .pathParam("terminationId", testabilityTerminationDTO.terminationId().toString())
              .when()
              .post("/testability/v1/terminations/{terminationId}/sendNotification")
              .then()
              .statusCode(HttpStatus.OK.value());
        }

        if (receiptReceived) {
          given()
              .contentType(ContentType.JSON)
              .pathParam("terminationId", testabilityTerminationDTO.terminationId().toString())
              .when()
              .post("/testability/v1/terminations/{terminationId}/receipt")
              .then()
              .statusCode(HttpStatus.OK.value());
        }
      }
    }
    return this;
  }

  public void cleanUp() {
    terminationIds.forEach(this::delete);

    given()
        .baseUri("http://localhost:18000")
        .pathParam("careProvider", HSA_ID)
        .when()
        .delete("/testability-intygstjanst/v1/certificates/{careProvider}")
        .then()
        .statusCode(HttpStatus.OK.value());

    given()
        .baseUri("http://localhost:18000")
        .when()
        .delete("/testability-intygstjanst/v1/certificatetexts")
        .then()
        .statusCode(HttpStatus.OK.value());

    given()
        .baseUri("http://localhost:18000")
        .when()
        .delete("/testability-sjut/v1/files")
        .then()
        .statusCode(HttpStatus.OK.value());

    given()
        .baseUri("http://localhost:18000")
        .when()
        .delete("/testability-tellustalk/v1/sms")
        .then()
        .statusCode(HttpStatus.OK.value());

    given()
        .baseUri("http://localhost:18000")
        .when()
        .delete("/testability-email/v1/")
        .then()
        .statusCode(HttpStatus.OK.value());
  }

  public TestData terminationId(String terminationId) {
    terminationIds.add(terminationId);
    return this;
  }

  public List<String> terminationIds() {
    return Collections.unmodifiableList(terminationIds);
  }

  private void delete(String terminationId) {
    given()
        .pathParam("terminationId", terminationId)
        .when()
        .delete("/testability/v1/terminations/{terminationId}")
        .then()
        .statusCode(HttpStatus.OK.value());
  }

  private List<CertificateXmlDTO> certificates() {
    final List<CertificateXmlDTO> certificates = new ArrayList<>(certificatesCount);
    for (int i = 0; i < certificatesCount; i++) {
      certificates.add(new CertificateXmlDTO(
          UUID.randomUUID().toString(),
          false,
          "<xml>Certificate</xml>"
      ));
    }
    return certificates;
  }

  private List<CertificateTextXmlDTO> certificateTexts() {
    final List<CertificateTextXmlDTO> certificateTexts = new ArrayList<>(certificateTextsCount);
    for (int i = 0; i < certificateTextsCount; i++) {
      certificateTexts.add(
          new CertificateTextXmlDTO(
              "certificateType" + i,
              "certificateTypeVersion" + i,
              "<xml>Text</xml>"
          )
      );
    }
    return certificateTexts;
  }
}
