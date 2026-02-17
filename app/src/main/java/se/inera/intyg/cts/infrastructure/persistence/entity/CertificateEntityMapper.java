package se.inera.intyg.cts.infrastructure.persistence.entity;

import java.util.Base64;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateId;
import se.inera.intyg.cts.domain.model.CertificateXML;

public class CertificateEntityMapper {

  private CertificateEntityMapper() {
    throw new IllegalStateException("Mapper class");
  }

  public static CertificateEntity toEntity(Certificate certificate,
      TerminationEntity terminationEntity) {
    return new CertificateEntity(
        0L,
        certificate.certificateId().id(),
        certificate.revoked(),
        Base64.getEncoder().encodeToString(certificate.certificateXML().xml().getBytes()),
        terminationEntity);
  }

  public static Certificate toDomain(CertificateEntity certificateEntity) {
    return new Certificate(
        new CertificateId(certificateEntity.getCertificateId()),
        certificateEntity.isRevoked(),
        new CertificateXML(new String(Base64.getDecoder().decode(certificateEntity.getXml())))
    );
  }
}
