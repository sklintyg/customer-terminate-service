package se.inera.intyg.cts.testability.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateTextEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;
import se.inera.intyg.cts.testability.service.TestabilityTerminationService;

@Configuration
@Profile("testability")
public class TestabilityConfig {

  @Bean
  public TestabilityTerminationService createTestablityTerminationService(
      TerminationEntityRepository terminationEntityRepository,
      CertificateEntityRepository certificateEntityRepository,
      CertificateTextEntityRepository certificateTextEntityRepository) {
    return new TestabilityTerminationService(terminationEntityRepository,
        certificateEntityRepository, certificateTextEntityRepository);
  }
}
