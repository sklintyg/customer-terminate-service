package se.inera.intyg.cts.infrastructure.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntityMapper.toDomain;
import static se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntityMapper.toEntity;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.DEFAULT_CERTIFICATE_TYPE;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.DEFAULT_CERTIFICATE_TYPE_VERSION;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.DEFAULT_XML;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.DEFAULT_XML_AS_BASE64;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.defaultCertificateText;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.defaultCertificateTextEntity;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.model.CertificateText;

class CertificateTextEntityMapperTest {

  @Nested
  class ToEntity {

    private CertificateText certificate;
    private TerminationEntity terminationEntity;

    @BeforeEach
    void setUp() {
      certificate = defaultCertificateText();
      terminationEntity = defaultTerminationEntity();
    }

    @Test
    void shallMapCertificateType() {
      assertEquals(DEFAULT_CERTIFICATE_TYPE,
          toEntity(certificate, terminationEntity).getCertificateType());
    }

    @Test
    void shallMapCertificateTypeVersion() {
      assertEquals(DEFAULT_CERTIFICATE_TYPE_VERSION,
          toEntity(certificate, terminationEntity).getCertificateTypeVersion());
    }

    @Test
    void shallMapXml() {
      assertEquals(DEFAULT_XML_AS_BASE64, toEntity(certificate, terminationEntity).getXml());
    }
  }

  @Nested
  class ToDomain {

    private CertificateTextEntity certificateTextEntity;

    @BeforeEach
    void setUp() {
      certificateTextEntity = defaultCertificateTextEntity();
    }

    @Test
    void shallMapCertificateType() {
      assertEquals(DEFAULT_CERTIFICATE_TYPE,
          toDomain(certificateTextEntity).certificateType().type());
    }

    @Test
    void shallMapCertificateTypeVersion() {
      assertEquals(DEFAULT_CERTIFICATE_TYPE_VERSION,
          toDomain(certificateTextEntity).certificateTypeVersion().version());
    }

    @Test
    void shallMapXml() {
      assertEquals(DEFAULT_XML, toDomain(certificateTextEntity).certificateXML().xml());
    }
  }
}