package se.inera.intyg.cts.domain.model;

public record CertificateText(CertificateType certificateType,
                              CertificateTypeVersion certificateTypeVersion,
                              CertificateXML certificateXML) {

  public CertificateText {
    if (certificateType == null) {
      throw new IllegalArgumentException("CertificateType cannot be null");
    }
    if (certificateTypeVersion == null) {
      throw new IllegalArgumentException("CertificateTypeVersion cannot be null");
    }
    if (certificateXML == null) {
      throw new IllegalArgumentException("CertificateXML cannot be null");
    }
  }
}
