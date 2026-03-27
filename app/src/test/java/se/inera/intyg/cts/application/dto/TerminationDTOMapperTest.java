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
package se.inera.intyg.cts.application.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.cts.application.dto.TerminationDTOMapper.toDTO;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_NAME;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_ORGANIZATION_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PERSON_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PHONE_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_STATUS;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.model.Termination;

class TerminationDTOMapperTest {

  @Nested
  class ToDTO {

    private Termination termination;

    @BeforeEach
    void setUp() {
      termination = defaultTermination();
    }

    @Test
    void shallMapTerminationId() {
      assertEquals(termination.terminationId().id(), toDTO(termination).terminationId());
    }

    @Test
    void shallMapHSAId() {
      assertEquals(DEFAULT_HSA_ID, toDTO(termination).hsaId());
    }

    @Test
    void shallMapOrganizationNumber() {
      assertEquals(DEFAULT_ORGANIZATION_NUMBER, toDTO(termination).organizationNumber());
    }

    @Test
    void shallMapPersonId() {
      assertEquals(DEFAULT_PERSON_ID, toDTO(termination).personId());
    }

    @Test
    void shallMapPhoneNumber() {
      assertEquals(DEFAULT_PHONE_NUMBER, toDTO(termination).phoneNumber());
    }

    @Test
    void shallMapCreated() {
      assertEquals(termination.created(), toDTO(termination).created());
    }

    @Test
    void shallMapCreator() {
      assertEquals(DEFAULT_CREATOR_HSA_ID, toDTO(termination).creatorHSAId());
    }

    @Test
    void shallMapCreatorName() {
      assertEquals(DEFAULT_CREATOR_NAME, toDTO(termination).creatorName());
    }

    @Test
    void shallStatus() {
      assertEquals(DEFAULT_STATUS.description(), toDTO(termination).status());
    }
  }
}
