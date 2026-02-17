package se.inera.intyg.cts.domain.model;

public record CertificateXML(String xml) {

  public CertificateXML {
    if (xml == null || xml.isBlank()) {
      throw new IllegalArgumentException("XML cannot be null or blank");
    }
  }
}
