package se.inera.intyg.cts.domain.model;

public record CertificateId(String id) {

  public CertificateId {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("CertificateId cannot be null or blank");
    }
  }
}
