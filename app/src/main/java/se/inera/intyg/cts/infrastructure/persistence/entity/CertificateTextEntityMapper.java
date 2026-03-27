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
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.CertificateType;
import se.inera.intyg.cts.domain.model.CertificateTypeVersion;
import se.inera.intyg.cts.domain.model.CertificateXML;

public class CertificateTextEntityMapper {

  private CertificateTextEntityMapper() {
    throw new IllegalStateException("Mapper class");
  }

  public static CertificateTextEntity toEntity(
      CertificateText certificateText, TerminationEntity terminationEntity) {
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
        new CertificateXML(new String(Base64.getDecoder().decode(certificateTextEntity.getXml()))));
  }
}
