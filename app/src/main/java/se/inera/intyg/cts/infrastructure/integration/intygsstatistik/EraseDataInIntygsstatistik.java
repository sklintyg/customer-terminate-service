package se.inera.intyg.cts.infrastructure.integration.intygsstatistik;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_DELETION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.ServiceId;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.service.EraseDataInService;
import se.inera.intyg.cts.domain.service.EraseException;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;
import se.inera.intyg.cts.logging.PerformanceLogging;

@Service
public class EraseDataInIntygsstatistik implements EraseDataInService {

  private final static Logger LOG = LoggerFactory.getLogger(EraseDataInIntygsstatistik.class);
  private final static ServiceId SERVICE_ID = new ServiceId("intygsstatistik");

  private final TerminationEntityRepository terminationEntityRepository;
  private final CertificateEntityRepository certificateEntityRepository;

  private final WebClient webClient;
  private final String scheme;
  private final String baseUrl;
  private final String port;
  private final String eraseCertificatesEndpoint;
  private final String eraseCareProviderEndpoint;
  private final int batchSize;

  public EraseDataInIntygsstatistik(
      TerminationEntityRepository terminationEntityRepository,
      CertificateEntityRepository certificateEntityRepository,
      @Qualifier(value = "intygsstatistikWebClient") WebClient webClient,
      @Value("${integration.intygsstatistik.scheme}") String scheme,
      @Value("${integration.intygsstatistik.baseurl}") String baseUrl,
      @Value("${integration.intygsstatistik.port}") String port,
      @Value("${integration.intygsstatistik.erase.certificates.endpoint}") String eraseCertificatesEndpoint,
      @Value("${integration.intygsstatistik.erase.careprovider.endpoint}") String eraseCareProviderEndpoint,
      @Value("${integration.intygsstatistik.erase.certificates.batchSize}") int batchSize) {
    this.terminationEntityRepository = terminationEntityRepository;
    this.certificateEntityRepository = certificateEntityRepository;
    this.webClient = webClient;
    this.scheme = scheme;
    this.baseUrl = baseUrl;
    this.port = port;
    this.eraseCertificatesEndpoint = eraseCertificatesEndpoint;
    this.eraseCareProviderEndpoint = eraseCareProviderEndpoint;
    this.batchSize = batchSize;
  }

  @Override
  public ServiceId serviceId() {
    return SERVICE_ID;
  }

  /**
   * In Intygsstatistik we first need to remove all related certificates before we remove the
   * careprovider. The reason is to not cause issues in Intygstatistik by removing careprovider
   * metadata before all certificates have been erased.
   * <p>
   * There can be a large number of certificates that needs to be erased, therefor we erase them in
   * batches (size based on configuration).
   * <p>
   * If any errors occurs during erasing, or that we only partially have deleted the certificates,
   * the implementation will throw an exception.
   *
   * @param termination Termination which data should be erased.
   * @throws EraseException
   */
  @Override
  @PerformanceLogging(eventAction = "erase-in-intygsstatistik", eventType = EVENT_TYPE_DELETION)
  public void erase(Termination termination) throws EraseException {
    final var certificatesToDelete = getCertificatesToDelete(termination.terminationId());

    final var deletedCertificates = new ArrayList<>(certificatesToDelete.size());
    batches(certificatesToDelete, batchSize)
        .forEach(certificatesBatch ->
            deletedCertificates.addAll(deleteBatch(certificatesBatch)
            )
        );

    if (isAllCertificatesDeleted(certificatesToDelete, deletedCertificates)) {
      deleteCareProvider(termination.careProvider().hsaId());
    } else {
      throw new EraseException(
          String.format("Only successfully deleted '%s/%s' certificates!",
              deletedCertificates.size(), certificatesToDelete.size())
      );
    }
  }

  private boolean isAllCertificatesDeleted(List<CertificateEntity> certificatesToDelete,
      ArrayList<Object> deletedCertificates) {
    return certificatesToDelete.size() == deletedCertificates.size();
  }

  private List<CertificateEntity> deleteBatch(List<CertificateEntity> certificateEntities)
      throws EraseException {
    final var deletedCertificateIds = deleteCertificates(
        certificateEntities.stream()
            .map(CertificateEntity::getCertificateId)
            .collect(Collectors.toList())
    );

    final var deletedCertificates = certificateEntities.stream()
        .filter(certificateEntity ->
            deletedCertificateIds.contains(certificateEntity.getCertificateId())
        )
        .collect(Collectors.toList());

    certificateEntityRepository.deleteAll(deletedCertificates);

    return deletedCertificates;
  }

  private List<String> deleteCertificates(List<String> certificateIds) throws EraseException {
    try {
      return webClient
          .method(HttpMethod.DELETE)
          .uri(uriBuilder -> uriBuilder
              .scheme(scheme)
              .host(baseUrl)
              .port(port)
              .path(eraseCertificatesEndpoint)
              .build()
          )
          .body(Mono.just(certificateIds), new ParameterizedTypeReference<List<String>>() {
          })
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .retrieve()
          .toEntity(new ParameterizedTypeReference<List<String>>() {
          })
          .block()
          .getBody();
    } catch (Exception ex) {
      LOG.error("Error calling intygsstatistik to delete certificates.", ex);
      throw new EraseException(
          String.format("Erase certificates failed with message '%s'", ex.getMessage())
      );
    }
  }

  private void deleteCareProvider(HSAId hsaId) throws EraseException {
    try {
      final var deletedCareproviderIds = webClient
          .method(HttpMethod.DELETE)
          .uri(uriBuilder -> uriBuilder
              .scheme(scheme)
              .host(baseUrl)
              .port(port)
              .path(eraseCareProviderEndpoint)
              .build(hsaId.id())
          )
          .body(Mono.just(Collections.singletonList(hsaId.id())),
              new ParameterizedTypeReference<List<String>>() {
              })
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .retrieve()
          .toEntity(new ParameterizedTypeReference<List<String>>() {
          })
          .block()
          .getBody();

      if (!deletedCareproviderIds.contains(hsaId.id())) {
        throw new EraseException(
            String.format(
                "Erase care provider failed because hsaId '%s' is missing in response '%s'",
                hsaId.id(), deletedCareproviderIds)
        );
      }
    } catch (Exception ex) {
      LOG.error("Error calling intygsstatistik to delete care provider.", ex);
      throw new EraseException(
          String.format("Erase care provider failed with message '%s'", ex.getMessage())
      );
    }
  }

  private List<CertificateEntity> getCertificatesToDelete(TerminationId terminationId) {
    final var terminationEntity = getTerminationEntity(terminationId);
    return certificateEntityRepository.findAllByTermination(terminationEntity);
  }

  private TerminationEntity getTerminationEntity(TerminationId terminationId) {
    return terminationEntityRepository.findByTerminationId(terminationId.id()).orElseThrow();
  }

  private Stream<List<CertificateEntity>> batches(List<CertificateEntity> certificates, int size) {
    final var noOfCertificates = certificates.size();
    if (noOfCertificates <= 0) {
      return Stream.empty();
    }
    final var fullChunks = (noOfCertificates - 1) / size;
    return IntStream.range(0, fullChunks + 1).mapToObj(
        n -> certificates.subList(n * size, n == fullChunks ? noOfCertificates : (n + 1) * size)
    );
  }
}
