package se.inera.intyg.cts.domain.model;

public record CertificateTypeVersion(String version) {

  public CertificateTypeVersion {
    if (version == null || version.isBlank()) {
      throw new IllegalArgumentException("Version cannot be null or empty");
    }
  }
}
