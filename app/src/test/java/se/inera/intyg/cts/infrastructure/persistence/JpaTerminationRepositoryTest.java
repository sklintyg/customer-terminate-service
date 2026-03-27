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
package se.inera.intyg.cts.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper.toEntity;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_TERMINATION_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@DataJpaTest
class JpaTerminationRepositoryTest {

  @Autowired private TerminationEntityRepository terminationEntityRepository;

  private JpaTerminationRepository jpaTerminationRepository;
  private Termination termination;

  @BeforeEach
  void setUp() {
    jpaTerminationRepository = new JpaTerminationRepository(terminationEntityRepository);
    termination = defaultTermination();
  }

  @Test
  void shallStoreNewTermination() {
    jpaTerminationRepository.store(termination);
    assertEquals(1, terminationEntityRepository.count());
  }

  @Test
  void shallStoreUpdatedTermination() {
    terminationEntityRepository.save(toEntity(termination));
    jpaTerminationRepository.store(termination);
    assertEquals(1, terminationEntityRepository.count());
  }

  @Test
  void shallReturnTerminationWhenFindingByTerminationId() {
    terminationEntityRepository.save(defaultTerminationEntity());
    assertNotNull(
        jpaTerminationRepository.findByTerminationId(new TerminationId(DEFAULT_TERMINATION_ID)),
        "Should return stored Termination");
  }

  @Test
  void shallReturnAllTerminationsWhenFindingAll() {
    final var numberOfTerminations = 10;
    for (int i = 0; i < numberOfTerminations; i++) {
      terminationEntityRepository.save(defaultTerminationEntity(UUID.randomUUID()));
    }

    assertEquals(numberOfTerminations, jpaTerminationRepository.findAll().size());
  }

  @Test
  void shallReturnTerminationsWithCorrectStatus() {
    final var numberOfTerminations = 10;
    for (int i = 0; i < numberOfTerminations; i++) {
      terminationEntityRepository.save(defaultTerminationEntity(UUID.randomUUID()));
    }

    assertEquals(
        numberOfTerminations,
        jpaTerminationRepository.findByStatuses(Arrays.asList(TerminationStatus.CREATED)).size());
  }

  @Test
  void shallReturnNoTerminationsWithIncorrectStatus() {
    final var numberOfTerminations = 10;
    for (int i = 0; i < numberOfTerminations; i++) {
      terminationEntityRepository.save(defaultTerminationEntity(UUID.randomUUID()));
    }

    assertEquals(
        0,
        jpaTerminationRepository
            .findByStatuses(Arrays.asList(TerminationStatus.COLLECTING_CERTIFICATES))
            .size());
  }
}
