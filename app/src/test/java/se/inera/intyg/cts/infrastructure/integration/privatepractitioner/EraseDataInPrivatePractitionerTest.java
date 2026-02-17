package se.inera.intyg.cts.infrastructure.integration.privatepractitioner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.service.EraseException;

class EraseDataInPrivatePractitionerTest {

  private static MockWebServer mockPrivatlakarportal;

  private static final String SCHEME = "http";
  private static final String BASE_URL = "localhost";
  private static final String ERASE_ENDPOINT = "/internalapi/privatepractitioner/certificates";

  private EraseDataInPrivatePractitioner eraseDataInPrivatePractitioner;
  private Termination termination;

  @BeforeAll
  static void beforeAll() throws IOException {
    mockPrivatlakarportal = new MockWebServer();
    ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
        ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.toLevel("info"));
    mockPrivatlakarportal.start();
  }

  @AfterAll
  static void afterAll() throws IOException {
    mockPrivatlakarportal.shutdown();
  }

  @BeforeEach
  void setUp() throws IOException {
    final var webClient = WebClient.create();

    eraseDataInPrivatePractitioner = new EraseDataInPrivatePractitioner(webClient, SCHEME, BASE_URL,
        Integer.toString(mockPrivatlakarportal.getPort()), ERASE_ENDPOINT);

    termination = defaultTermination();
  }

  @Test
  void shallCallPrivatlakarportalToEraseCareProvider() throws InterruptedException {
    mockPrivatlakarportal.enqueue(new MockResponse());

    eraseDataInPrivatePractitioner.erase(termination);

    final var pathSegments = mockPrivatlakarportal.takeRequest().getRequestUrl().toString();

    assertTrue(pathSegments.contains(termination.careProvider().hsaId().id()),
        () -> String.format("HSA-Id '%s' was not included in the url '%s'",
            termination.careProvider().hsaId().id(), pathSegments));
  }

  @Test
  void shallThrowEraseExceptionIfEraseFailed() {
    mockPrivatlakarportal.enqueue(new MockResponse()
        .setResponseCode(500)
    );

    assertThrows(EraseException.class,
        () -> eraseDataInPrivatePractitioner.erase(termination)
    );
  }

  @Test
  void shallReturnWebcertAsServiceId() {
    assertEquals("privatlakarportal", eraseDataInPrivatePractitioner.serviceId().id());
  }
}