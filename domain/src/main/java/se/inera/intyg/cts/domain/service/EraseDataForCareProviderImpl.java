package se.inera.intyg.cts.domain.service;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.cts.domain.model.EraseService;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class EraseDataForCareProviderImpl implements EraseDataForCareProvider {

  private static final Logger LOG = LoggerFactory.getLogger(EraseDataForCareProviderImpl.class);

  private final List<EraseDataInService> services;
  private final CertificateBatchRepository certificateBatchRepository;
  private final TerminationRepository terminationRepository;

  public EraseDataForCareProviderImpl(List<EraseDataInService> services,
      CertificateBatchRepository certificateBatchRepository,
      TerminationRepository terminationRepository) {
    this.services = services;
    this.certificateBatchRepository = certificateBatchRepository;
    this.terminationRepository = terminationRepository;
  }

  /**
   * The implementation has the responsibility to erase all data in related services that belong to
   * the termination.
   * <p>
   * The first time the service is called with a specific termination, it will initialize to erase
   * by checking what services that should be erased.
   * <p>
   * Any subsequent time the service is called with a specific termination, it will use the service
   * provider to erase all data in each of the services (i.e. Webcert, Intygstjanst) that is not
   * marked as erased.
   * <p>
   * If it is successful in erasing a service, it will mark the service as erased.
   * <p>
   * When all services has been erased the termination will receive a status update.
   * <p>
   * If for some reason the data has changed since exported, the erase will be cancelled. This is to
   * make sure that the exported data has the latest changes for the certificates.
   *
   * @param termination Termination to erase data for.
   */
  @Override
  public void erase(Termination termination) {
    if (termination.status().equals(TerminationStatus.START_ERASE)) {
      initializeErasingCareProvider(termination);
    } else {
      eraseCareProvider(termination);
    }

    terminationRepository.store(termination);
  }

  private void initializeErasingCareProvider(Termination termination) {
    if (verifyNoChangeSinceExport(termination)) {
      termination.eraseCancelled();
      return;
    }

    termination.startErase(
        services.stream()
            .map(eraseDataInService -> new EraseService(eraseDataInService.serviceId(), false))
            .toList()
    );
  }

  private boolean verifyNoChangeSinceExport(Termination termination) {
    final var certificateSummary = certificateBatchRepository.certificateSummary(termination);
    if (!certificateSummary.equals(termination.export().certificateSummary())) {
      LOG.error(
          "Certificates for termination '{}' has changed since export. Exported '{}' and current '{}'. Erase will be cancelled!",
          termination.terminationId().id(), termination.export().certificateSummary(),
          certificateSummary);
      return true;
    }
    return false;
  }

  private void eraseCareProvider(Termination termination) {
    services.stream()
        .filter(filterOutAlreadyErasedServices(termination))
        .forEach(eraseDataInService(termination));
  }

  private Predicate<EraseDataInService> filterOutAlreadyErasedServices(Termination termination) {
    return eraseDataInService ->
        termination.erase().eraseServices().stream()
            .anyMatch(
                eraseService -> eraseDataInService.serviceId().equals(eraseService.serviceId())
                    && !eraseService.erased()
            );
  }

  private Consumer<EraseDataInService> eraseDataInService(Termination termination) {
    return eraseDataInService -> {
      try {
        eraseDataInService.erase(termination);
        termination.erased(eraseDataInService.serviceId());
        LOG.info("Erased care provider for termination '{}' in service '{}'",
            termination.terminationId().id(),
            eraseDataInService.serviceId().id());
      } catch (EraseException e) {
        LOG.error(String.format(
            "Could not erase care provider for termination '%s' in service '%s' due to message '%s'",
            termination.terminationId().id(), eraseDataInService.serviceId().id(),
            e.getMessage()));
      }
    };
  }
}
