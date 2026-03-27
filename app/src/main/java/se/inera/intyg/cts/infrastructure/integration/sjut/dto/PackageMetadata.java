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
package se.inera.intyg.cts.infrastructure.integration.sjut.dto;

public record PackageMetadata(
    String organizationName,
    String organizationNumber,
    String delegatePnr,
    String sourceSystem,
    String receiptUrl) {

  public PackageMetadata {
    if (organizationName == null || organizationName.isBlank()) {
      throw new IllegalArgumentException("OrganizationName cannot be null or blank");
    }
    if (organizationNumber == null || organizationNumber.isBlank()) {
      throw new IllegalArgumentException("OrganizationNumber cannot be null or blank");
    }
    if (delegatePnr == null || delegatePnr.isBlank()) {
      throw new IllegalArgumentException("DelegatePnr cannot be null or blank");
    }
    if (sourceSystem == null || sourceSystem.isBlank()) {
      throw new IllegalArgumentException("SourceSystem cannot be null or blank");
    }
    if (receiptUrl == null || receiptUrl.isBlank()) {
      throw new IllegalArgumentException("ReceiptUrl cannot be null or blank");
    }

    delegatePnr = delegatePnr.replace("-", "");
  }
}
