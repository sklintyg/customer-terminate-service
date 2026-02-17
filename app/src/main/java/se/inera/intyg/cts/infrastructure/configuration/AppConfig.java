package se.inera.intyg.cts.infrastructure.configuration;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.CollectExportContent;
import se.inera.intyg.cts.domain.service.CollectExportContentImpl;
import se.inera.intyg.cts.domain.service.CreatePackage;
import se.inera.intyg.cts.domain.service.EraseDataForCareProvider;
import se.inera.intyg.cts.domain.service.EraseDataForCareProviderImpl;
import se.inera.intyg.cts.domain.service.EraseDataInService;
import se.inera.intyg.cts.domain.service.ExportPackage;
import se.inera.intyg.cts.domain.service.ExportPackageImpl;
import se.inera.intyg.cts.domain.service.PasswordGenerator;
import se.inera.intyg.cts.domain.service.SendNotification;
import se.inera.intyg.cts.domain.service.SendPackageNotification;
import se.inera.intyg.cts.domain.service.SendPackageNotificationImpl;
import se.inera.intyg.cts.domain.service.SendPackagePassword;
import se.inera.intyg.cts.domain.service.SendPackagePasswordImpl;
import se.inera.intyg.cts.domain.service.SendPassword;
import se.inera.intyg.cts.domain.service.UpdateTermination;
import se.inera.intyg.cts.domain.service.UpdateTerminationImpl;
import se.inera.intyg.cts.domain.service.UploadPackage;

@Configuration
public class AppConfig {

  @Bean
  public CollectExportContent collectExportContent(TerminationRepository terminationRepository,
      CertificateBatchRepository certificationBatchRepository,
      CertificateRepository certificationRepository,
      CertificateTextRepository certificateTextRepository) {
    return new CollectExportContentImpl(terminationRepository, certificationBatchRepository,
        certificationRepository, certificateTextRepository);
  }

  @Bean
  public ExportPackage exportPackage(CreatePackage createPackage,
      UploadPackage uploadPackage,
      TerminationRepository terminationRepository, PasswordGenerator passwordGenerator) {
    return new ExportPackageImpl(createPackage, uploadPackage, terminationRepository,
        passwordGenerator);
  }

  @Bean
  public EraseDataForCareProvider eraseDataForCareProvider(
      List<EraseDataInService> eraseDataInServices,
      CertificateBatchRepository certificateBatchRepository,
      TerminationRepository terminationRepository) {
    return new EraseDataForCareProviderImpl(eraseDataInServices, certificateBatchRepository,
        terminationRepository);
  }

  @Bean
  public SendPackageNotification sendNotifications(SendNotification sendNotification,
      TerminationRepository terminationRepository) {
    return new SendPackageNotificationImpl(sendNotification, terminationRepository);
  }

  @Bean
  public SendPackagePassword sendPackagePassword(SendPassword sendPassword,
      TerminationRepository terminationRepository) {
    return new SendPackagePasswordImpl(sendPassword, terminationRepository);
  }

  @Bean
  public UpdateTermination updateTermination(TerminationRepository terminationRepository,
      CertificateRepository certificateRepository,
      CertificateTextRepository certificateTextRepository) {
    return new UpdateTerminationImpl(terminationRepository, certificateRepository,
        certificateTextRepository);
  }
}
