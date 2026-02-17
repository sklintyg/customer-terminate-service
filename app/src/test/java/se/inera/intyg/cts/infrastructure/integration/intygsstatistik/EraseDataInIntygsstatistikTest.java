package se.inera.intyg.cts.infrastructure.integration.intygsstatistik;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper.toEntity;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.defaultCertificateEntity;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.service.EraseException;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@ExtendWith(MockitoExtension.class)
class EraseDataInIntygsstatistikTest {

  @Mock
  private TerminationEntityRepository terminationEntityRepository;

  @Mock
  private CertificateEntityRepository certificateEntityRepository;

  private static MockWebServer mockIntygsstatistik;

  private static final String SCHEME = "http";
  private static final String BASE_URL = "localhost";
  private static final String ERASE_CERTIFICATE_ENDPOINT = "/api/internalapi/v1/intygsidlist";
  private static final String ERASE_CARE_PROVIDER_ENDPOINT = "/api/internalapi/v1/vardgivareidlist";
  private static final int BATCH_SIZE = 5;

  private EraseDataInIntygsstatistik eraseDataInIntygsstatistik;
  private Termination termination;
  private TerminationEntity terminationEntity;

  @BeforeEach
  void setUp() throws IOException {
    mockIntygsstatistik = new MockWebServer();
    ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
        ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.toLevel("info"));
    mockIntygsstatistik.start();

    final var webClient = WebClient.create();

    eraseDataInIntygsstatistik = new EraseDataInIntygsstatistik(terminationEntityRepository,
        certificateEntityRepository, webClient, SCHEME, BASE_URL,
        Integer.toString(mockIntygsstatistik.getPort()), ERASE_CERTIFICATE_ENDPOINT,
        ERASE_CARE_PROVIDER_ENDPOINT, BATCH_SIZE);

    termination = defaultTermination();
    terminationEntity = toEntity(termination);
  }

  @AfterEach
  void beforeEach() throws IOException {
    mockIntygsstatistik.shutdown();
  }

  @Nested
  class EraseCertificatesSuccessful {

    private static final String FIRST_BATCH_OF_IDS = "[\"ID1\",\"ID2\",\"ID3\",\"ID4\",\"ID5\"]";
    private static final MockResponse FIRST_BATCH_RESPONSE = new MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    private static final String SECOND_BATCH_OF_IDS = "[\"ID6\",\"ID7\"]";
    private static final String CARE_PROVIDER_ID = "[\"" + DEFAULT_HSA_ID + "\"]";

    private List<CertificateEntity> certificateEntities;

    @BeforeEach
    void setUp() {
      certificateEntities = Arrays.asList(
          defaultCertificateEntity("ID1"),
          defaultCertificateEntity("ID2"),
          defaultCertificateEntity("ID3"),
          defaultCertificateEntity("ID4"),
          defaultCertificateEntity("ID5"),
          defaultCertificateEntity("ID6"),
          defaultCertificateEntity("ID7")
      );

      final var mDispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) {
          final var body = request.getBody().readUtf8();
          if (body.equalsIgnoreCase(FIRST_BATCH_OF_IDS)) {
            return new MockResponse().setBody(FIRST_BATCH_OF_IDS)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
          }
          if (body.equalsIgnoreCase(SECOND_BATCH_OF_IDS)) {
            return new MockResponse().setBody(SECOND_BATCH_OF_IDS)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
          }
          if (body.equalsIgnoreCase(CARE_PROVIDER_ID)) {
            return new MockResponse().setBody(
                    CARE_PROVIDER_ID)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
          }
          // Any other calls are unexpected and should return an error!
          return new MockResponse().setStatus("500");
        }
      };
      mockIntygsstatistik.setDispatcher(mDispatcher);

      doReturn(Optional.of(terminationEntity))
          .when(terminationEntityRepository).findByTerminationId(termination.terminationId().id());
      doReturn(certificateEntities)
          .when(certificateEntityRepository).findAllByTermination(terminationEntity);
    }

    @Test
    void shallEraseCertificatesInIntygsstatistik() {
      final var iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);

      eraseDataInIntygsstatistik.erase(termination);

      verify(certificateEntityRepository, times(2)).deleteAll(iterableArgumentCaptor.capture());

      assertDeletedCertificates(certificateEntities,
          getDeletedCertificateEntities(iterableArgumentCaptor));
    }

    @Test
    void shallDeleteCareProviderAfterCertificatesHasBeenSuccessfullyErased() {
      final var expectedRequestCount = 3;

      eraseDataInIntygsstatistik.erase(termination);

      assertEquals(expectedRequestCount, mockIntygsstatistik.getRequestCount(), () ->
          String.format("Expected '%s' requests but received '%s'", expectedRequestCount,
              mockIntygsstatistik.getRequestCount()));
    }
  }

  @Nested
  class EraseCertificatesPartlySuccessful {

    private static final String FIRST_BATCH_OF_IDS = "[\"ID1\",\"ID2\",\"ID3\",\"ID4\",\"ID5\"]";
    private static final String FIRST_BATCH_OF_IDS_RESPONSE = "[\"ID1\",\"ID3\",\"ID5\"]";
    private static final String SECOND_BATCH_OF_IDS = "[\"ID6\",\"ID7\"]";

    private List<CertificateEntity> certificateEntities;

    @BeforeEach
    void setUp() {
      certificateEntities = Arrays.asList(
          defaultCertificateEntity("ID1"),
          defaultCertificateEntity("ID2"),
          defaultCertificateEntity("ID3"),
          defaultCertificateEntity("ID4"),
          defaultCertificateEntity("ID5"),
          defaultCertificateEntity("ID6"),
          defaultCertificateEntity("ID7")
      );

      final var mDispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) {
          final var body = request.getBody().readUtf8();
          if (body.equalsIgnoreCase(FIRST_BATCH_OF_IDS)) {
            return new MockResponse().setBody(FIRST_BATCH_OF_IDS_RESPONSE)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
          }
          if (body.equalsIgnoreCase(SECOND_BATCH_OF_IDS)) {
            return new MockResponse().setBody(SECOND_BATCH_OF_IDS)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
          }
          // Any other calls are unexpected and should return an error!
          return new MockResponse().setStatus("500");
        }
      };
      mockIntygsstatistik.setDispatcher(mDispatcher);

      doReturn(Optional.of(terminationEntity))
          .when(terminationEntityRepository).findByTerminationId(termination.terminationId().id());
      doReturn(certificateEntities)
          .when(certificateEntityRepository).findAllByTermination(terminationEntity);
    }

    @Test
    void shallThrowEraseExceptionWhenNotAllCertificatesAreSuccessfullyErased() {
      final var eraseException = assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      assertTrue(
          eraseException.getMessage().contains("Only successfully deleted '5/7' certificates!"),
          () -> eraseException.getMessage());
    }

    @Test
    void shallOnlyDeleteCertificatesThatWasSuccessfullyErased() {
      final var successfullyRemovedCertificates = Arrays.asList(
          defaultCertificateEntity("ID1"),
          defaultCertificateEntity("ID3"),
          defaultCertificateEntity("ID5"),
          defaultCertificateEntity("ID6"),
          defaultCertificateEntity("ID7")
      );

      final var iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);

      assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      verify(certificateEntityRepository, times(2)).deleteAll(iterableArgumentCaptor.capture());

      assertDeletedCertificates(successfullyRemovedCertificates,
          getDeletedCertificateEntities(iterableArgumentCaptor));
    }

    @Test
    void shallNotDeleteCareProviderUntilAllCertificatesHasBeenSuccessfullyErased() {
      final var expectedRequestCount = 2;
      assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      assertEquals(expectedRequestCount, mockIntygsstatistik.getRequestCount(), () ->
          String.format("Expected '%s' requests but received '%s'", expectedRequestCount,
              mockIntygsstatistik.getRequestCount()));
    }
  }

  @Nested
  class EraseCertificatesFailed {

    private List<CertificateEntity> certificateEntities;

    @BeforeEach
    void setUp() {
      certificateEntities = Arrays.asList(
          defaultCertificateEntity("ID1"),
          defaultCertificateEntity("ID2"),
          defaultCertificateEntity("ID3"),
          defaultCertificateEntity("ID4"),
          defaultCertificateEntity("ID5"),
          defaultCertificateEntity("ID6"),
          defaultCertificateEntity("ID7")
      );

      doReturn(Optional.of(terminationEntity))
          .when(terminationEntityRepository).findByTerminationId(termination.terminationId().id());
      doReturn(certificateEntities)
          .when(certificateEntityRepository).findAllByTermination(terminationEntity);

      mockIntygsstatistik.enqueue(new MockResponse().setResponseCode(500));
    }

    @Test
    void shallThrowEraseExceptionWhenErasingCertificateFails() {
      final var eraseException = assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      assertTrue(eraseException.getMessage().contains("Erase certificates failed with message"),
          () -> eraseException.getMessage());
    }

    @Test
    void shallNotDeleteCertificatesWhenErasingCertificateFails() {
      assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      verify(certificateEntityRepository, times(0)).deleteAll(any());
    }

    @Test
    void shallNotDeleteCareProviderUntilAllCertificatesHasBeenSuccessfullyErased() {
      final var expectedRequestCount = 1;
      assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      assertEquals(expectedRequestCount, mockIntygsstatistik.getRequestCount(), () ->
          String.format("Expected '%s' requests but received '%s'", expectedRequestCount,
              mockIntygsstatistik.getRequestCount()));
    }
  }

  @Nested
  class EraseCareProviderFailed {

    private List<CertificateEntity> certificateEntities;

    @BeforeEach
    void setUp() {
      certificateEntities = Collections.emptyList();

      doReturn(Optional.of(terminationEntity))
          .when(terminationEntityRepository).findByTerminationId(termination.terminationId().id());
      doReturn(certificateEntities)
          .when(certificateEntityRepository).findAllByTermination(terminationEntity);
    }

    @Test
    void shallThrowEraseExceptionWhenErasingCareproviderFails() {
      mockIntygsstatistik.enqueue(new MockResponse().setResponseCode(500));
      final var eraseException = assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );
      assertTrue(eraseException.getMessage().contains("Erase care provider failed with message"),
          () -> eraseException.getMessage());
    }

    @Test
    void shallThrowEraseExceptionWhenErasingCareproviderIsMissingInResponse() {
      mockIntygsstatistik.enqueue(new MockResponse().setBody("[]")
          .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

      final var eraseException = assertThrows(EraseException.class,
          () -> eraseDataInIntygsstatistik.erase(termination)
      );

      assertTrue(eraseException.getMessage().contains("Erase care provider failed with message"),
          () -> eraseException.getMessage());
    }
  }

  @Test
  void shallReturnIntygsstatistikAsServiceId() {
    assertEquals("intygsstatistik", eraseDataInIntygsstatistik.serviceId().id());
  }

  private List<CertificateEntity> getDeletedCertificateEntities(
      ArgumentCaptor<Iterable> iterableArgumentCaptor) {
    final var deletedCertificates = new ArrayList<CertificateEntity>();
    iterableArgumentCaptor.getAllValues()
        .forEach(
            iterable -> iterable.forEach(o -> deletedCertificates.add((CertificateEntity) o))
        );
    return deletedCertificates;
  }

  private void assertDeletedCertificates(List<CertificateEntity> expected,
      List<CertificateEntity> actual) {
    assertEquals(expected.size(), actual.size());
    expected.forEach(certificateEntity ->
        assertTrue(actual.stream()
                .anyMatch(actualCertificateEntity -> actualCertificateEntity.getCertificateId()
                    .equalsIgnoreCase(certificateEntity.getCertificateId())
                ),
            () -> String.format("Certificate with id '%s' was not deleted when expected!",
                certificateEntity.getCertificateId()))
    );
  }
}