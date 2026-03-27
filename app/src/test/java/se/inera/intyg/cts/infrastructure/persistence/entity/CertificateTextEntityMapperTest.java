/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
      assertEquals(
          DEFAULT_CERTIFICATE_TYPE, toEntity(certificate, terminationEntity).getCertificateType());
    }

    @Test
    void shallMapCertificateTypeVersion() {
      assertEquals(
          DEFAULT_CERTIFICATE_TYPE_VERSION,
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
      assertEquals(
          DEFAULT_CERTIFICATE_TYPE, toDomain(certificateTextEntity).certificateType().type());
    }

    @Test
    void shallMapCertificateTypeVersion() {
      assertEquals(
          DEFAULT_CERTIFICATE_TYPE_VERSION,
          toDomain(certificateTextEntity).certificateTypeVersion().version());
    }

    @Test
    void shallMapXml() {
      assertEquals(DEFAULT_XML, toDomain(certificateTextEntity).certificateXML().xml());
    }
  }
}
