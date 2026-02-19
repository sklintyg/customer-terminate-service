package se.inera.intyg.cts.testutil;

import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.random.RandomGenerator;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.CertificateType;
import se.inera.intyg.cts.domain.model.CertificateTypeVersion;
import se.inera.intyg.cts.domain.model.CertificateXML;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;

public class CertificateTextTestDataBuilder {

  public static Long DEFAULT_ID = RandomGenerator.getDefault().nextLong(1, 5000);
  public static String DEFAULT_CERTIFICATE_TYPE = "certificateType";
  public static String DEFAULT_CERTIFICATE_TYPE_VERSION = "1.5";
  public static String DEFAULT_XML = "<xml></xml>";
  public static String DEFAULT_XML_AS_BASE64 = Base64.getEncoder()
      .encodeToString(DEFAULT_XML.getBytes(StandardCharsets.UTF_8));

  public static CertificateText defaultCertificateText() {
    return new CertificateText(
        new CertificateType(DEFAULT_CERTIFICATE_TYPE),
        new CertificateTypeVersion(DEFAULT_CERTIFICATE_TYPE_VERSION),
        new CertificateXML(DEFAULT_XML)
    );
  }

  public static CertificateTextEntity defaultCertificateTextEntity() {
    return new CertificateTextEntity(
        DEFAULT_ID,
        DEFAULT_CERTIFICATE_TYPE,
        DEFAULT_CERTIFICATE_TYPE_VERSION,
        DEFAULT_XML_AS_BASE64,
        defaultTerminationEntity()
    );
  }

  public static List<CertificateText> certificateTexts(int total) {
    final List<CertificateText> certificateTexts = new ArrayList<>();
    for (int i = 0; i < total; i++) {
      certificateTexts.add(
          new CertificateText(
              new CertificateType(DEFAULT_CERTIFICATE_TYPE + i),
              new CertificateTypeVersion(DEFAULT_CERTIFICATE_TYPE_VERSION),
              new CertificateXML(DEFAULT_XML)
          )
      );
    }
    return certificateTexts;
  }

  public static List<CertificateTextEntity> certificateTextEntities(
      TerminationEntity terminationEntity, int total) {
    final List<CertificateTextEntity> certificates = new ArrayList<>();
    for (int i = 0; i < total; i++) {
      certificates.add(
          new CertificateTextEntity(
              null,
              DEFAULT_CERTIFICATE_TYPE + i,
              DEFAULT_CERTIFICATE_TYPE_VERSION,
              DEFAULT_XML_AS_BASE64,
              terminationEntity
          )
      );
    }
    return certificates;
  }
}
