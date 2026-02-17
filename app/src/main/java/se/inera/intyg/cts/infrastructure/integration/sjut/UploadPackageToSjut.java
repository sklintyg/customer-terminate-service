package se.inera.intyg.cts.infrastructure.integration.sjut;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_CHANGE;

import java.io.File;
import java.net.URI;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.service.UploadPackage;
import se.inera.intyg.cts.infrastructure.integration.sjut.dto.PackageMetadata;
import se.inera.intyg.cts.logging.PerformanceLogging;

@Service
public class UploadPackageToSjut implements UploadPackage {

  private static final Logger LOG = LoggerFactory.getLogger(UploadPackageToSjut.class);

  public static final String FILE_PREFIX = "file:";
  public static final String FILE_PART = "file";
  public static final String METADATA_PART = "metadata";

  private final WebClient webClient;
  private final String scheme;
  private final String baseUrl;
  private final String port;
  private final String uploadEndpoint;
  private final String sourceSystem;
  private final String receiptBaseUrl;

  public UploadPackageToSjut(
      @Qualifier(value = "sjutWebClient") WebClient webClient,
      @Value("${integration.sjut.scheme}") String scheme,
      @Value("${integration.sjut.baseurl}") String baseUrl,
      @Value("${integration.sjut.port}") String port,
      @Value("${integration.sjut.upload.endpoint}") String uploadEndpoint,
      @Value("${integration.sjut.source.system}") String sourceSystem,
      @Value("${integration.sjut.receipt.baseurl}") String receiptBaseUrl) {
    this.webClient = webClient;
    this.scheme = scheme;
    this.baseUrl = baseUrl;
    this.port = port;
    this.uploadEndpoint = uploadEndpoint;
    this.sourceSystem = sourceSystem;
    this.receiptBaseUrl = receiptBaseUrl;
  }

  @Override
  @PerformanceLogging(eventAction = "upload-package", eventType = EVENT_TYPE_CHANGE)
  public void uploadPackage(Termination termination, File packageToUpload) {
    final var resource = getResource(packageToUpload);
    final var packageMetadata = getPackageMetadata(termination);

    final var multipartBodyBuilder = new MultipartBodyBuilder();
    multipartBodyBuilder.part(FILE_PART, resource);
    multipartBodyBuilder.part(METADATA_PART, packageMetadata);

    final var clientReponse = webClient.post()
        .uri(this::getUri)
        .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
        .exchangeToMono(clientResponse ->
            handleResponse(termination, clientResponse))
        .share()
        .block();

    LOG.info("File for termination '{}' was uploaded to Sjut with result '{}'",
        termination.terminationId().id(), clientReponse);
  }

  private Resource getResource(File packageToUpload) {
    return new DefaultResourceLoader().getResource(FILE_PREFIX + packageToUpload.getAbsolutePath());
  }

  private PackageMetadata getPackageMetadata(Termination termination) {
    return new PackageMetadata(
        termination.careProvider().hsaId().id(),
        termination.careProvider().organizationNumber().number(),
        termination.export().organizationRepresentative().personId().id(),
        sourceSystem,
        receiptBaseUrl + termination.terminationId().id()
    );
  }

  private URI getUri(UriBuilder uriBuilder) {
    uriBuilder = uriBuilder
        .scheme(scheme)
        .host(baseUrl)
        .path(uploadEndpoint);

    if (!port.isBlank()) {
      uriBuilder.port(port);
    }

    return uriBuilder.build();
  }

  private Mono<String> handleResponse(Termination termination, ClientResponse clientResponse) {
    if (clientResponse.statusCode() == HttpStatus.OK) {
      return clientResponse.bodyToMono(String.class);
    }

    final var message = String.format(
        "Could not upload file for termination '%s' to Sjut. Received status code '%s'.",
        termination.terminationId().id(), clientResponse.statusCode());
    LOG.error(message);
    throw new ServiceException(message);
  }
}
