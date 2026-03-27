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
  public static String DEFAULT_XML_AS_BASE64 =
      Base64.getEncoder().encodeToString(DEFAULT_XML.getBytes(StandardCharsets.UTF_8));

  public static CertificateText defaultCertificateText() {
    return new CertificateText(
        new CertificateType(DEFAULT_CERTIFICATE_TYPE),
        new CertificateTypeVersion(DEFAULT_CERTIFICATE_TYPE_VERSION),
        new CertificateXML(DEFAULT_XML));
  }

  public static CertificateTextEntity defaultCertificateTextEntity() {
    return new CertificateTextEntity(
        DEFAULT_ID,
        DEFAULT_CERTIFICATE_TYPE,
        DEFAULT_CERTIFICATE_TYPE_VERSION,
        DEFAULT_XML_AS_BASE64,
        defaultTerminationEntity());
  }

  public static List<CertificateText> certificateTexts(int total) {
    final List<CertificateText> certificateTexts = new ArrayList<>();
    for (int i = 0; i < total; i++) {
      certificateTexts.add(
          new CertificateText(
              new CertificateType(DEFAULT_CERTIFICATE_TYPE + i),
              new CertificateTypeVersion(DEFAULT_CERTIFICATE_TYPE_VERSION),
              new CertificateXML(DEFAULT_XML)));
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
              terminationEntity));
    }
    return certificates;
  }
}
