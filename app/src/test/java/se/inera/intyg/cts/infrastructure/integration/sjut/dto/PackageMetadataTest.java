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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PackageMetadataTest {

  private static final String ORGANIZATION_NAME = "Organization name";
  private static final String ORGANIZATION_NUMBER = "Organization number";
  private static final String DELEGATE_PNR = "191212121212";
  private static final String SOURCE_SYSTEM = "Source system";
  private static final String RECEIPT_URL = "https://test/api/v1/receipt";

  @Test
  void shallRemoveDashFromDelegateNumber() {
    final var packageMetadata =
        new PackageMetadata(
            ORGANIZATION_NAME, ORGANIZATION_NUMBER, "19121212-1212", SOURCE_SYSTEM, RECEIPT_URL);
    assertEquals(DELEGATE_PNR, packageMetadata.delegatePnr());
  }
}
