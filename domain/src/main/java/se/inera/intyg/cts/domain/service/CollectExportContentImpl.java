package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class CollectExportContentImpl implements CollectExportContent {

  private final TerminationRepository terminationRepository;
  private final CertificateBatchRepository certificateBatchRepository;
  private final CertificateRepository certificateRepository;
  private final CertificateTextRepository certificateTextRepository;

  public CollectExportContentImpl(TerminationRepository terminationRepository,
      CertificateBatchRepository certificateBatchRepository,
      CertificateRepository certificateRepository,
      CertificateTextRepository certificateTextRepository) {
    this.terminationRepository = terminationRepository;
    this.certificateBatchRepository = certificateBatchRepository;
    this.certificateRepository = certificateRepository;
    this.certificateTextRepository = certificateTextRepository;
  }

  @Override
  public void collectCertificates(TerminationId terminationId) {
    final var termination = getTermination(terminationId);

    final var certificateBatch = certificateBatchRepository.nextBatch(termination);
    termination.collect(certificateBatch);

    certificateRepository.store(termination, certificateBatch.certificateList());
    terminationRepository.store(termination);
  }

  @Override
  public void collectCertificateTexts(Termination termination) {
    final var certificateTexts = certificateBatchRepository.certificateTexts(termination);
    termination.collect(certificateTexts);

    certificateTextRepository.store(termination, certificateTexts);
    terminationRepository.store(termination);
  }

  private Termination getTermination(TerminationId terminationId) {
    return terminationRepository.findByTerminationId(terminationId)
        .orElseThrow(() ->
            new IllegalArgumentException("Termination with id: '%s' doesn't exist!"
                .formatted(terminationId))
        );
  }
}
