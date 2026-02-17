package se.inera.intyg.cts.integrationtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import net.lingala.zip4j.ZipFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ExportIT {

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
  void shallCollectCertificatesToExport() {
    testData
        .defaultTermination()
        .certificates(10)
        .setup();

    given()
        .when()
        .post("/api/v1/exports/collectCertificates")
        .then()
        .statusCode(HttpStatus.OK.value());

    assertEquals(10, getCertificatesCount(testData.terminationIds().get(0)));
  }

  @Test
  void shallCollectCertificatesToExportInMultipleBatches() {
    testData
        .defaultTermination()
        .certificates(90)
        .setup();

    for (int i = 0; i < 5; i++) {
      given()
          .when()
          .post("/api/v1/exports/collectCertificates")
          .then()
          .statusCode(HttpStatus.OK.value());

      assertEquals(i == 4 ? 90 : (i + 1) * 20,
          getCertificatesCount(testData.terminationIds().get(0)));
    }
  }

  @Test
  void shallCollectCertificateTextsToExport() {
    testData
        .defaultTermination()
        .certificates(90)
        .collectCertificates()
        .certificateTexts(10)
        .setup();

    given()
        .when()
        .post("/api/v1/exports/collectCertificateTexts")
        .then()
        .statusCode(HttpStatus.OK.value());

    assertEquals(10, getCertificateTextsCount());
  }

  @Test
  void shallExportPackageToSjut() {
    testData
        .defaultTermination()
        .certificates(90)
        .collectCertificates()
        .certificateTexts(10)
        .collectCertificateTexts()
        .setup();

    given()
        .when()
        .post("/api/v1/exports/exportPackage")
        .then()
        .statusCode(HttpStatus.OK.value());

    assertTrue(getUploadedFileAsBytes().length > 0,
        "Uploaded file should be larger than zero bytes");
  }

  @Test
  void shallExportPackageToSjutWithGeneratedPassword() {
    testData
        .defaultTermination()
        .certificates(90)
        .collectCertificates()
        .certificateTexts(10)
        .collectCertificateTexts()
        .setup();

    given()
        .when()
        .post("/api/v1/exports/exportPackage")
        .then()
        .statusCode(HttpStatus.OK.value());

    final var uploadedFile = getUploadedFile();
    final var password = getPassword(testData.terminationIds().get(0));

    assertDoesNotThrow(() -> extractUploadedFile(uploadedFile, password),
        () -> String.format("Could not extract uploaded file with password: %s", password));
  }

  private File getUploadedFile() {
    try {
      final var uploadedFileAsBytes = getUploadedFileAsBytes();
      final var uploadedFile = File.createTempFile(TestData.ORG_NO, ".zip");
      try (final var fos = new FileOutputStream(uploadedFile)) {
        fos.write(uploadedFileAsBytes);
      }
      return uploadedFile;
    } catch (Exception ex) {
      throw new RuntimeException("Failed to download and create uploaded file", ex);
    }
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

  private void extractUploadedFile(File uploadedFile, String password) throws IOException {
    try (final var zipFIle = new ZipFile(uploadedFile.getPath(), password.toCharArray())) {
      zipFIle.extractAll(Files.createTempDirectory("tmpDirPrefix").toFile().getAbsolutePath());
    }
  }

  private byte[] getUploadedFileAsBytes() {
    return given()
        .baseUri("http://localhost:18000")
        .pathParam("organizationNumber", TestData.ORG_NO)
        .when()
        .get("/testability-sjut/v1/files/{organizationNumber}")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .asByteArray();
  }

  private Integer getCertificatesCount(String terminationId) {
    return given()
        .pathParam("terminationId", terminationId)
        .when()
        .get("/testability/v1/terminations/{terminationId}/certificatesCount")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .response().as(Integer.class);
  }

  private Integer getCertificateTextsCount() {
    return given()
        .pathParam("terminationId", testData.terminationIds().get(0))
        .when()
        .get("/testability/v1/terminations/{terminationId}/certificateTextsCount")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .response().as(Integer.class);
  }
}
