package se.inera.intyg.cts.infrastructure.integration;

import java.util.List;
import java.util.stream.Collectors;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateBatch;
import se.inera.intyg.cts.domain.model.CertificateSummary;

public class GetCertificateBatchFromMemory implements GetCertificateBatch {

  private List<Certificate> certificates;

  @Override
  public CertificateBatch get(String careProvider, int limit, int offset) {
    return new CertificateBatch(
        new CertificateSummary(
            certificates.size(),
            (int) certificates.stream().filter(Certificate::revoked).count()
        ),
        certificates.stream()
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList())
    );
  }

  public void prepare(List<Certificate> certificates) {
    this.certificates = certificates;
  }
}
