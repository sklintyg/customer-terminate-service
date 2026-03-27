/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.infrastructure.integration.webcert;

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

class EraseDataInWebcertTest {

  private static MockWebServer mockWebcert;

  private static final String SCHEME = "http";
  private static final String BASE_URL = "localhost";
  private static final String ERASE_ENDPOINT = "/internalapi/v1/certificates";

  private EraseDataInWebcert eraseDataInWebcert;
  private Termination termination;

  @BeforeAll
  static void beforeAll() throws IOException {
    mockWebcert = new MockWebServer();
    ch.qos.logback.classic.Logger rootLogger =
        (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.toLevel("info"));
    mockWebcert.start();
  }

  @AfterAll
  static void afterAll() throws IOException {
    mockWebcert.shutdown();
  }

  @BeforeEach
  void setUp() throws IOException {
    final var webClient = WebClient.create();

    eraseDataInWebcert =
        new EraseDataInWebcert(
            webClient, SCHEME, BASE_URL, Integer.toString(mockWebcert.getPort()), ERASE_ENDPOINT);

    termination = defaultTermination();
  }

  @Test
  void shallCallWebcertToEraseCareProvider() throws InterruptedException {
    mockWebcert.enqueue(new MockResponse());

    eraseDataInWebcert.erase(termination);

    final var pathSegments = mockWebcert.takeRequest().getRequestUrl().toString();

    assertTrue(
        pathSegments.contains(termination.careProvider().hsaId().id()),
        () ->
            String.format(
                "HSA-Id '%s' was not included in the url '%s'",
                termination.careProvider().hsaId().id(), pathSegments));
  }

  @Test
  void shallThrowEraseExceptionIfEraseFailed() {
    mockWebcert.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(EraseException.class, () -> eraseDataInWebcert.erase(termination));
  }

  @Test
  void shallReturnWebcertAsServiceId() {
    assertEquals("webcert", eraseDataInWebcert.serviceId().id());
  }
}
