package se.inera.intyg.cts.infrastructure.integration.Intygstjanst;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_ACCESSED;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateBatch;
import se.inera.intyg.cts.domain.model.CertificateId;
import se.inera.intyg.cts.domain.model.CertificateSummary;
import se.inera.intyg.cts.domain.model.CertificateXML;
import se.inera.intyg.cts.infrastructure.integration.GetCertificateBatch;
import se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto.CertificateExportPageDTO;
import se.inera.intyg.cts.logging.PerformanceLogging;

@Service
public class GetCertificateBatchFromIntygstjanst implements GetCertificateBatch {

  private final WebClient webClient;
  private final String scheme;
  private final String baseUrl;
  private final String port;
  private final String certificatesEndpoint;

  public GetCertificateBatchFromIntygstjanst(
      @Qualifier(value = "intygstjanstWebClient") WebClient webClient,
      @Value("${integration.intygstjanst.scheme}") String scheme,
      @Value("${integration.intygstjanst.baseurl}") String baseUrl,
      @Value("${integration.intygstjanst.port}") String port,
      @Value("${integration.intygstjanst.certificates.endpoint}") String certificatesEndpoint) {
    this.webClient = webClient;
    this.scheme = scheme;
    this.baseUrl = baseUrl;
    this.port = port;
    this.certificatesEndpoint = certificatesEndpoint;
  }

  @Override
  @PerformanceLogging(eventAction = "get-certificate-batch", eventType = EVENT_TYPE_ACCESSED)
  public CertificateBatch get(String careProvider, int limit, int collected) {
    final var certificateExportPageDTOMono = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .scheme(scheme)
            .host(baseUrl)
            .port(port)
            .path(certificatesEndpoint + "/{careProvider}")
            .queryParam("batchSize", limit)
            .queryParam("collected", collected)
            .build(careProvider))
        .retrieve()
        .bodyToMono(CertificateExportPageDTO.class)
        .share()
        .block();

    return new CertificateBatch(
        new CertificateSummary(
            (int) certificateExportPageDTOMono.total(),
            (int) certificateExportPageDTOMono.totalRevoked()
        ),
        certificateExportPageDTOMono.certificateXmls().stream()
            .map(certificateXmlDTO -> new Certificate(
                new CertificateId(certificateXmlDTO.id()),
                certificateXmlDTO.revoked(),
                new CertificateXML(certificateXmlDTO.xml())
            ))
            .collect(Collectors.toList())
    );
  }
}