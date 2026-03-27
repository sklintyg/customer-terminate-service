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

import java.util.Base64;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateId;
import se.inera.intyg.cts.domain.model.CertificateXML;

public class CertificateEntityMapper {

  private CertificateEntityMapper() {
    throw new IllegalStateException("Mapper class");
  }

  public static CertificateEntity toEntity(
      Certificate certificate, TerminationEntity terminationEntity) {
    return new CertificateEntity(
        null,
        certificate.certificateId().id(),
        certificate.revoked(),
        Base64.getEncoder().encodeToString(certificate.certificateXML().xml().getBytes()),
        terminationEntity);
  }

  public static Certificate toDomain(CertificateEntity certificateEntity) {
    return new Certificate(
        new CertificateId(certificateEntity.getCertificateId()),
        certificateEntity.isRevoked(),
        new CertificateXML(new String(Base64.getDecoder().decode(certificateEntity.getXml()))));
  }
}
