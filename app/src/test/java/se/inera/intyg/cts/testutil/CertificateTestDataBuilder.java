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
import java.util.UUID;
import java.util.random.RandomGenerator;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateId;
import se.inera.intyg.cts.domain.model.CertificateXML;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;

public class CertificateTestDataBuilder {

  public static Long DEFAULT_ID = RandomGenerator.getDefault().nextLong(1, 5000);
  public static String DEFAULT_CERTIFICATE_ID = UUID.randomUUID().toString();
  public static boolean DEFAULT_REVOKED = false;
  public static String DEFAULT_XML = "<xml></xml>";
  public static String DEFAULT_XML_AS_BASE64 =
      Base64.getEncoder().encodeToString(DEFAULT_XML.getBytes(StandardCharsets.UTF_8));

  public static Certificate defaultCertificate() {
    return new Certificate(
        new CertificateId(DEFAULT_CERTIFICATE_ID),
        DEFAULT_REVOKED,
        new CertificateXML(DEFAULT_XML));
  }

  public static CertificateEntity defaultCertificateEntity() {
    return new CertificateEntity(
        DEFAULT_ID,
        DEFAULT_CERTIFICATE_ID,
        DEFAULT_REVOKED,
        DEFAULT_XML_AS_BASE64,
        defaultTerminationEntity());
  }

  public static CertificateEntity defaultCertificateEntity(String id) {
    return new CertificateEntity(
        RandomGenerator.getDefault().nextLong(1, 5000),
        id,
        DEFAULT_REVOKED,
        DEFAULT_XML_AS_BASE64,
        defaultTerminationEntity());
  }

  public static List<Certificate> certificates(int total, int revokeCount) {
    final List<Certificate> certificates = new ArrayList<>();
    for (int i = 0; i < total; i++) {
      certificates.add(
          new Certificate(
              new CertificateId(UUID.randomUUID().toString()),
              i >= (total - revokeCount),
              new CertificateXML(DEFAULT_XML)));
    }
    return certificates;
  }

  public static List<CertificateEntity> certificateEntities(
      TerminationEntity terminationEntity, int total, int revokeCount) {
    final List<CertificateEntity> certificates = new ArrayList<>();
    for (int i = 0; i < total; i++) {
      certificates.add(
          new CertificateEntity(
              null,
              UUID.randomUUID().toString(),
              i >= (total - revokeCount),
              DEFAULT_XML_AS_BASE64,
              terminationEntity));
    }
    return certificates;
  }
}
