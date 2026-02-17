package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.EmailAddress;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.PersonId;
import se.inera.intyg.cts.domain.model.PhoneNumber;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class UpdateTerminationImpl implements UpdateTermination {

  private final TerminationRepository terminationRepository;
  private final CertificateRepository certificateRepository;
  private final CertificateTextRepository certificateTextRepository;

  public UpdateTerminationImpl(TerminationRepository terminationRepository,
      CertificateRepository certificateRepository,
      CertificateTextRepository certificateTextRepository) {
    this.terminationRepository = terminationRepository;
    this.certificateRepository = certificateRepository;
    this.certificateTextRepository = certificateTextRepository;
  }

  /**
   * Updates the termination. If the termination´s status been reset to collect certificates, and
   * its texts, any already collected data will be removed.
   */
  @Override
  public Termination update(Termination termination, HSAId hsaId, PersonId personId,
      EmailAddress emailAddress, PhoneNumber phoneNumber) {

    termination.update(hsaId, personId, emailAddress, phoneNumber);

    final var storeTermination = terminationRepository.store(termination);
    if (storeTermination.status() == TerminationStatus.CREATED) {
      certificateRepository.remove(storeTermination);
      certificateTextRepository.remove(storeTermination);
    }

    return storeTermination;
  }
}
