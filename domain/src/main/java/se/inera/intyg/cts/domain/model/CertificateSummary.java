package se.inera.intyg.cts.domain.model;

public record CertificateSummary(int total, int revoked) {

  public CertificateSummary {
    if (total < 0) {
      throw new IllegalArgumentException("Total cannot be less than zero");
    }
    if (revoked < 0) {
      throw new IllegalArgumentException("Revoked cannot be less than zero");
    }
  }

  public CertificateSummary add(CertificateSummary certificateSummary) {
    return new CertificateSummary(
        total + certificateSummary.total(),
        revoked + certificateSummary.revoked()
    );
  }
}
