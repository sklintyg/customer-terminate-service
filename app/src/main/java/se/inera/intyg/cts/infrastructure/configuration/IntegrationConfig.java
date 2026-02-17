package se.inera.intyg.cts.infrastructure.configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import se.inera.intyg.cts.infrastructure.integration.ExchangeFilterFunctionProvider;

@Configuration
public class IntegrationConfig {

  private final static Logger LOG = LoggerFactory.getLogger(IntegrationConfig.class);

  public static final int IN_MEMORY_SIZE_TO_MANAGE_LARGE_XML_RESPONSES = 16 * 1024 * 1024;

  @Value("${webclient.keystore.type:PKCS12}")
  private String keyStoreType;

  @Value("${webclient.keystore.password}")
  private String keyStorePassword;

  @Value("${webclient.keystore.path}")
  private String keyStorePath;

  @Value("${webclient.truststore.password}")
  private String trustStorePassword;

  @Value("${webclient.truststore.path}")
  private String trustStorePath;

  @Bean(name = "intygstjanstWebClient")
  public WebClient webClientForIntygstjanst() {
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(codecs ->
            codecs.defaultCodecs().maxInMemorySize(IN_MEMORY_SIZE_TO_MANAGE_LARGE_XML_RESPONSES)
        )
        .build();

    return WebClient.builder()
        .filter(ExchangeFilterFunctionProvider.addHeadersFromMDCToRequest())
        .exchangeStrategies(strategies)
        .build();
  }

  @Bean(name = "sjutWebClient")
  public WebClient webClientForSjut() {
    final var sslContext = getSslContext();
    final var httpClient = HttpClient.create()
        .secure(sslSpec -> sslSpec.sslContext(sslContext));

    return WebClient.builder()
        .filter(ExchangeFilterFunctionProvider.addHeadersFromMDCToRequest())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  @Bean(name = "tellusTalkWebClient")
  public WebClient webClientForTellusTalk() {
    return WebClient.builder()
        .filter(ExchangeFilterFunctionProvider.addHeadersFromMDCToRequest())
        .build();
  }

  @Bean(name = "webcertWebClient")
  public WebClient webClientForWebcert() {
    return WebClient.builder()
        .filter(ExchangeFilterFunctionProvider.addHeadersFromMDCToRequest())
        .build();
  }

  @Bean(name = "intygsstatistikWebClient")
  public WebClient webClientForIntygsstatistik() {
    return WebClient.builder()
        .filter(ExchangeFilterFunctionProvider.addHeadersFromMDCToRequest())
        .build();
  }

  @Bean(name = "privatePractitionerWebClient")
  public WebClient webClientForPrivatePractitioner() {
    return WebClient.builder()
        .filter(ExchangeFilterFunctionProvider.addHeadersFromMDCToRequest())
        .build();
  }

  private KeyManagerFactory getKeyManagerFactory() {
    try {
      final var keyManagerFactory = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());

      final var keyStore = KeyStore.getInstance(keyStoreType);
      keyStore.load(new FileInputStream(ResourceUtils.getFile(keyStorePath)),
          keyStorePassword.toCharArray());

      keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

      return keyManagerFactory;
    } catch (Exception ex) {
      LOG.error("Could not initialize keystore!", ex);
      throw new RuntimeException("Could not initialize keystore!", ex);
    }
  }

  private TrustManagerFactory getTrustManagerFactory() {
    try {
      final var trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());

      final var trustStore = KeyStore.getInstance(keyStoreType);
      trustStore.load(new FileInputStream((ResourceUtils.getFile(trustStorePath))),
          trustStorePassword.toCharArray());

      trustManagerFactory.init(trustStore);

      return trustManagerFactory;
    } catch (Exception ex) {
      LOG.error("Could not initialize truststore!", ex);
      throw new RuntimeException("Could not initialize truststore!", ex);
    }
  }

  private SslContext getSslContext() {
    try {
      return SslContextBuilder
          .forClient()
          .keyManager(getKeyManagerFactory())
          .trustManager(getTrustManagerFactory())
          .build();
    } catch (Exception ex) {
      LOG.error("Could not build SslContext.", ex);
      throw new RuntimeException("Could not build SslContext.", ex);
    }
  }
}
