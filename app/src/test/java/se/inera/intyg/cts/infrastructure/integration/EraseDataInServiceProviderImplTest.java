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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.service.EraseDataInService;

class EraseDataInServiceProviderImplTest {

  @Test
  void shouldReturnRegisteredEraseDataInServices() {
    final var expectedServices =
        Arrays.asList(
            mock(EraseDataInService.class),
            mock(EraseDataInService.class),
            mock(EraseDataInService.class));
    final var actualServices = new EraseDataInServiceProviderImpl(expectedServices).getServices();

    assertEquals(expectedServices.size(), actualServices.size());
    assertAll(
        () -> assertEquals(expectedServices.get(0), actualServices.get(0)),
        () -> assertEquals(expectedServices.get(1), actualServices.get(1)),
        () -> assertEquals(expectedServices.get(2), actualServices.get(2)));
  }
}
