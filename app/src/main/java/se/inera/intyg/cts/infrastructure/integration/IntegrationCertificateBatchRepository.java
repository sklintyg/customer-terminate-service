package se.inera.intyg.cts.infrastructure.integration;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.domain.model.CertificateBatch;
import se.inera.intyg.cts.domain.model.CertificateSummary;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;

@Repository
public class IntegrationCertificateBatchRepository implements CertificateBatchRepository {

  private final GetCertificateBatch getCertificateBatch;
  private final int batchSize;
  private final GetCertificateTexts getCertificateTexts;

  public IntegrationCertificateBatchRepository(GetCertificateBatch getCertificateBatch,
      @Value("${certificate.batch.size:30}") int batchSize,
      GetCertificateTexts getCertificateTexts) {
    this.getCertificateBatch = getCertificateBatch;
    this.batchSize = batchSize;
    this.getCertificateTexts = getCertificateTexts;
  }

  @Override
  public CertificateSummary certificateSummary(
      Termination termination) {
    return getCertificateBatch.get(termination.careProvider().hsaId().id(), 1, 0)
        .certificateSummary();
  }

  @Override
  public CertificateBatch nextBatch(Termination termination) {
    return getCertificateBatch.get(termination.careProvider().hsaId().id(), batchSize,
        termination.export().certificateSummary().total());
  }

  @Override
  public List<CertificateText> certificateTexts(Termination termination) {
    return getCertificateTexts.get();
  }
}
