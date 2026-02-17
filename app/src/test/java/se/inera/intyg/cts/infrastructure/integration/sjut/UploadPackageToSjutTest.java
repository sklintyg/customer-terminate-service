package se.inera.intyg.cts.infrastructure.integration.sjut;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import java.io.File;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import se.inera.intyg.cts.domain.model.Termination;

class UploadPackageToSjutTest {

  private static MockWebServer mockSjut;

  private static final String SCHEME = "http";
  private static final String BASE_URL = "localhost";
  private static final String UPLOAD_ENDPOINT = "/api/v1/upload";
  private static final String SOURCE_SYSTEM = "intyg";
  private static final String RECEIPT_BASE_URL = "/api/v1/receipt/";

  private UploadPackageToSjut uploadPackageToSjut;
  private Termination termination;
  private File packageFile;

  @BeforeAll
  static void beforeAll() throws IOException {
    mockSjut = new MockWebServer();
    mockSjut.start();
  }

  @AfterAll
  static void afterAll() throws IOException {
    mockSjut.shutdown();
  }

  @BeforeEach
  void setUp() throws IOException {
    WebClient webClient = WebClient.create();

    uploadPackageToSjut = new UploadPackageToSjut(webClient, SCHEME, BASE_URL,
        Integer.toString(mockSjut.getPort()), UPLOAD_ENDPOINT, SOURCE_SYSTEM, RECEIPT_BASE_URL);

    termination = defaultTermination();
    packageFile = File.createTempFile("dummy", "zip");
  }

  @AfterEach
  void tearDown() {
    if (packageFile != null && packageFile.exists()) {
      packageFile.delete();
    }
  }

  @Test
  void shallUploadFileToSjutWithFile() throws InterruptedException {
    mockSjut.enqueue(new MockResponse()
        .setBody("Package has been uploaded!")
    );

    uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile);

    final var requestBody = mockSjut.takeRequest().getBody().readUtf8();
    assertTrue(requestBody.contains(packageFile.getName()), requestBody);
  }

  @Test
  void shallUploadFileToSjutWithHsaId() throws InterruptedException {
    mockSjut.enqueue(new MockResponse()
        .setBody("Package has been uploaded!")
    );

    uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile);

    final var requestBody = mockSjut.takeRequest().getBody().readUtf8();
    assertTrue(requestBody.contains(termination.careProvider().hsaId().id()), requestBody);
  }

  @Test
  void shallUploadFileToSjutWithOrganizationNumber() throws InterruptedException {
    mockSjut.enqueue(new MockResponse()
        .setBody("Package has been uploaded!")
    );

    uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile);

    final var requestBody = mockSjut.takeRequest().getBody().readUtf8();
    assertTrue(requestBody.contains(termination.careProvider().organizationNumber().number()),
        requestBody
    );
  }

  @Test
  void shallUploadFileToSjutWithSourceSystem() throws InterruptedException {
    mockSjut.enqueue(new MockResponse()
        .setBody("Package has been uploaded!")
    );

    uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile);

    final var requestBody = mockSjut.takeRequest().getBody().readUtf8();
    assertTrue(requestBody.contains(SOURCE_SYSTEM), requestBody);
  }

  @Test
  void shallUploadFileToSjutWithDelegatePnr() throws InterruptedException {
    mockSjut.enqueue(new MockResponse()
        .setBody("Package has been uploaded!")
    );

    uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile);

    final var requestBody = mockSjut.takeRequest().getBody().readUtf8();
    assertTrue(
        requestBody.contains(termination.export().organizationRepresentative().personId().id()),
        requestBody
    );
  }

  @Test
  void shallUploadFileToSjutWithReceiptUrl() throws InterruptedException {
    mockSjut.enqueue(new MockResponse()
        .setBody("Package has been uploaded!")
    );

    uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile);

    final var requestBody = mockSjut.takeRequest().getBody().readUtf8();
    assertTrue(
        requestBody.contains(RECEIPT_BASE_URL + termination.terminationId().id().toString()),
        requestBody
    );
  }

  @Test
  void shallThrowExceptionIfUploadFailed() {
    mockSjut.enqueue(new MockResponse()
        .setBody("Upload failed!")
        .setResponseCode(500)
    );

    assertThrows(RuntimeException.class,
        () -> uploadPackageToSjut.uploadPackage(defaultTermination(), packageFile)
    );
  }
}