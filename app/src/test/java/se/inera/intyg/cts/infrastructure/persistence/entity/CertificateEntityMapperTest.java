package se.inera.intyg.cts.infrastructure.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntityMapper.toDomain;
import static se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntityMapper.toEntity;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.DEFAULT_CERTIFICATE_ID;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.DEFAULT_REVOKED;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.DEFAULT_XML;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.DEFAULT_XML_AS_BASE64;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.defaultCertificate;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.defaultCertificateEntity;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.model.Certificate;

class CertificateEntityMapperTest {

  @Nested
  class ToEntity {

    private Certificate certificate;
    private TerminationEntity terminationEntity;

    @BeforeEach
    void setUp() {
      certificate = defaultCertificate();
      terminationEntity = defaultTerminationEntity();
    }

    @Test
    void shallMapCertificateId() {
      assertEquals(DEFAULT_CERTIFICATE_ID,
          toEntity(certificate, terminationEntity).getCertificateId());
    }

    @Test
    void shallMapRevoked() {
      assertEquals(DEFAULT_REVOKED, toEntity(certificate, terminationEntity).isRevoked());
    }

    @Test
    void shallMapXml() {
      assertEquals(DEFAULT_XML_AS_BASE64, toEntity(certificate, terminationEntity).getXml());
    }
  }

  @Nested
  class ToDomain {

    private CertificateEntity certificateEntity;

    @BeforeEach
    void setUp() {
      certificateEntity = defaultCertificateEntity();
    }

    @Test
    void shallMapCertificateId() {
      assertEquals(DEFAULT_CERTIFICATE_ID, toDomain(certificateEntity).certificateId().id());
    }

    @Test
    void shallMapXml() {
      assertEquals(DEFAULT_XML, toDomain(certificateEntity).certificateXML().xml());
    }

    @Test
    void shallMapRevoked() {
      assertEquals(DEFAULT_REVOKED, toDomain(certificateEntity).revoked());
    }
  }
}