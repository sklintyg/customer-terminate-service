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
package se.inera.intyg.cts.domain.model;

public record CertificateSummary(int total, int revoked) {

  public CertificateSummary {
    if (total < 0) {
      throw new IllegalArgumentException("Total cannot be less than zero");
    }
    if (revoked < 0) {
      throw new IllegalArgumentException("Revoked cannot be less than zero");
    }
  }

  public CertificateSummary add(CertificateSummary certificateSummary) {
    return new CertificateSummary(
        total + certificateSummary.total(), revoked + certificateSummary.revoked());
  }
}
