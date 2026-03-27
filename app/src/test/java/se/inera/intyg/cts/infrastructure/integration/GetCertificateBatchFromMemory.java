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
package se.inera.intyg.cts.infrastructure.integration;

import java.util.List;
import java.util.stream.Collectors;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateBatch;
import se.inera.intyg.cts.domain.model.CertificateSummary;

public class GetCertificateBatchFromMemory implements GetCertificateBatch {

  private List<Certificate> certificates;

  @Override
  public CertificateBatch get(String careProvider, int limit, int offset) {
    return new CertificateBatch(
        new CertificateSummary(
            certificates.size(), (int) certificates.stream().filter(Certificate::revoked).count()),
        certificates.stream().skip(offset).limit(limit).collect(Collectors.toList()));
  }

  public void prepare(List<Certificate> certificates) {
    this.certificates = certificates;
  }
}
