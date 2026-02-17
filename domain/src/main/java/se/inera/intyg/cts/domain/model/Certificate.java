package se.inera.intyg.cts.domain.model;

public record Certificate(CertificateId certificateId, boolean revoked,
                          CertificateXML certificateXML) {

  public Certificate {
    if (certificateId == null) {
      throw new IllegalArgumentException("CertificateId cannot be null");
    }
    if (certificateXML == null) {
      throw new IllegalArgumentException("CertificateXML cannot be null");
    }
  }
}
