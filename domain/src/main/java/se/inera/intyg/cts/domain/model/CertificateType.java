package se.inera.intyg.cts.domain.model;

public record CertificateType(String type) {

  public CertificateType {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("CertificateId cannot be null or empty");
    }
  }
}
