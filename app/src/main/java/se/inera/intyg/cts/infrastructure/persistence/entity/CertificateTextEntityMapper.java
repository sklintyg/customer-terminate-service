package se.inera.intyg.cts.infrastructure.persistence.entity;

import java.util.Base64;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.CertificateType;
import se.inera.intyg.cts.domain.model.CertificateTypeVersion;
import se.inera.intyg.cts.domain.model.CertificateXML;

public class CertificateTextEntityMapper {

  private CertificateTextEntityMapper() {
    throw new IllegalStateException("Mapper class");
  }

  public static CertificateTextEntity toEntity(CertificateText certificateText,
      TerminationEntity terminationEntity) {
    return new CertificateTextEntity(
        null,
        certificateText.certificateType().type(),
        certificateText.certificateTypeVersion().version(),
        Base64.getEncoder().encodeToString(certificateText.certificateXML().xml().getBytes()),
        terminationEntity);
  }

  public static CertificateText toDomain(CertificateTextEntity certificateTextEntity) {
    return new CertificateText(
        new CertificateType(certificateTextEntity.getCertificateType()),
        new CertificateTypeVersion(certificateTextEntity.getCertificateTypeVersion()),
        new CertificateXML(new String(Base64.getDecoder().decode(certificateTextEntity.getXml())))
    );
  }
}
