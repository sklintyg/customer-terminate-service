package se.inera.intyg.cts.infrastructure.integration.Intygstjanst;

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

class EraseDataInIntygstjanstTest {

  private static MockWebServer mockIntygstjanst;

  private static final String SCHEME = "http";
  private static final String BASE_URL = "localhost";
  private static final String ERASE_ENDPOINT = "/inera-certificate/internalapi/v1/certificates";

  private EraseDataInIntygstjanst eraseDataInIntygstjanst;
  private Termination termination;

  @BeforeAll
  static void beforeAll() throws IOException {
    mockIntygstjanst = new MockWebServer();
    ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
        ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.toLevel("info"));
    mockIntygstjanst.start();
  }

  @AfterAll
  static void afterAll() throws IOException {
    mockIntygstjanst.shutdown();
  }

  @BeforeEach
  void setUp() throws IOException {
    final var webClient = WebClient.create();

    eraseDataInIntygstjanst = new EraseDataInIntygstjanst(webClient, SCHEME, BASE_URL,
        Integer.toString(mockIntygstjanst.getPort()), ERASE_ENDPOINT);

    termination = defaultTermination();
  }

  @Test
  void shallCallIntygstjanstToEraseCareProvider() throws InterruptedException {
    mockIntygstjanst.enqueue(new MockResponse());

    eraseDataInIntygstjanst.erase(termination);

    final var pathSegments = mockIntygstjanst.takeRequest().getRequestUrl().toString();

    assertTrue(pathSegments.contains(termination.careProvider().hsaId().id()),
        () -> String.format("HSA-Id '%s' was not included in the url '%s'",
            termination.careProvider().hsaId().id(), pathSegments));
  }

  @Test
  void shallThrowEraseExceptionIfEraseFailed() {
    mockIntygstjanst.enqueue(new MockResponse()
        .setResponseCode(500)
    );

    assertThrows(EraseException.class,
        () -> eraseDataInIntygstjanst.erase(termination)
    );
  }

  @Test
  void shallReturnIntygstjanstAsServiceId() {
    assertEquals("intygstjanst", eraseDataInIntygstjanst.serviceId().id());
  }
}