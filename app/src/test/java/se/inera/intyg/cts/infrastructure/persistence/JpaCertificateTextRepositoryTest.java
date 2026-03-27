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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.certificateTextEntities;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.certificateTexts;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationBuilder;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateTextEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@DataJpaTest
class JpaCertificateTextRepositoryTest {

  @Autowired private TerminationEntityRepository terminationEntityRepository;
  @Autowired private CertificateTextEntityRepository certificateTextEntityRepository;
  private JpaCertificateTextRepository jpaCertificateTextRepository;
  private Termination termination;
  private TerminationEntity terminationEntity;

  @BeforeEach
  void setUp() {
    jpaCertificateTextRepository =
        new JpaCertificateTextRepository(
            certificateTextEntityRepository, terminationEntityRepository);
    termination = defaultTermination();
    terminationEntity = defaultTerminationEntity();
  }

  @Test
  void shallStoreCertificateTextsForExistingTermination() {
    TerminationEntity te = terminationEntityRepository.save(terminationEntity);
    termination = defaultTerminationBuilder().terminationId(te.getTerminationId()).create();

    jpaCertificateTextRepository.store(termination, certificateTexts(3));

    assertEquals(3, certificateTextEntityRepository.count());
  }

  @Test
  @Transactional
  void shallReturnCertificateTextsForExistingTermination() {
    TerminationEntity te = terminationEntityRepository.save(terminationEntity);
    TerminationEntity savedTerminationEntity =
        terminationEntityRepository.findByTerminationId(terminationEntity.getTerminationId()).get();
    certificateTextEntityRepository.saveAll(certificateTextEntities(savedTerminationEntity, 3));

    termination = defaultTerminationBuilder().terminationId(te.getTerminationId()).create();
    assertEquals(3, jpaCertificateTextRepository.get(termination).size());
  }

  @Test
  void shallReturnEmptyForForMissingTermination() {
    assertEquals(0, jpaCertificateTextRepository.get(termination).size());
  }

  @Test
  void shallRemoveCertificateTextsForExistingTermination() {
    TerminationEntity te = terminationEntityRepository.save(terminationEntity);
    TerminationEntity savedTerminationEntity =
        terminationEntityRepository.findByTerminationId(terminationEntity.getTerminationId()).get();

    certificateTextEntityRepository.saveAll(certificateTextEntities(savedTerminationEntity, 3));

    termination = defaultTerminationBuilder().terminationId(te.getTerminationId()).create();
    jpaCertificateTextRepository.remove(termination);

    assertEquals(0, jpaCertificateTextRepository.get(termination).size());
  }

  @Test
  void shallThrowExceptionForMissingTerminationWhenRemovingCertificateTexts() {
    assertThrows(
        IllegalArgumentException.class, () -> jpaCertificateTextRepository.remove(termination));
  }
}
