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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.certificates;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.certificateTexts;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.model.CertificateBatch;
import se.inera.intyg.cts.domain.model.CertificateSummary;
import se.inera.intyg.cts.domain.model.Termination;

class IntegrationCertificateBatchRepositoryTest {

  private static final int BATCH_SIZE = 30;
  private IntegrationCertificateBatchRepository integrationCertificateBatchRepository;
  private GetCertificateBatchFromMemory getCertificateBatchFromMemory;
  private GetCertificateTextsFromMemory getCertificateTextsFromMemory;
  private Termination termination;

  @BeforeEach
  void setUp() {
    getCertificateBatchFromMemory = new GetCertificateBatchFromMemory();
    getCertificateTextsFromMemory = new GetCertificateTextsFromMemory();
    integrationCertificateBatchRepository =
        new IntegrationCertificateBatchRepository(
            getCertificateBatchFromMemory, BATCH_SIZE, getCertificateTextsFromMemory);
    termination = defaultTermination();
    getCertificateBatchFromMemory.prepare(certificates(80, 5));
    getCertificateTextsFromMemory.prepare(certificateTexts(10));
  }

  @Nested
  class FirstBatch {

    @Test
    void shallGetCertificateSummaryWithFirstBatch() {
      assertEquals(
          new CertificateSummary(80, 5),
          integrationCertificateBatchRepository.nextBatch(termination).certificateSummary());
    }

    @Test
    void shallGetCertificateWithFirstBatch() {
      assertEquals(
          30,
          integrationCertificateBatchRepository.nextBatch(termination).certificateList().size());
    }
  }

  @Nested
  class MiddleBatch {

    @BeforeEach
    void setUp() {
      termination
          .export()
          .processBatch(new CertificateBatch(new CertificateSummary(80, 5), certificates(30, 0)));
    }

    @Test
    void shallGetCertificateSummaryWithFirstBatch() {
      assertEquals(
          new CertificateSummary(80, 5),
          integrationCertificateBatchRepository.nextBatch(termination).certificateSummary());
    }

    @Test
    void shallGetCertificateWithFirstBatch() {
      assertEquals(
          30,
          integrationCertificateBatchRepository.nextBatch(termination).certificateList().size());
    }
  }

  @Nested
  class LastBatch {

    @BeforeEach
    void setUp() {
      termination
          .export()
          .processBatch(new CertificateBatch(new CertificateSummary(80, 5), certificates(60, 0)));
    }

    @Test
    void shallGetCertificateSummaryWithFirstBatch() {
      assertEquals(
          new CertificateSummary(80, 5),
          integrationCertificateBatchRepository.nextBatch(termination).certificateSummary());
    }

    @Test
    void shallGetCertificateWithFirstBatch() {
      assertEquals(
          20,
          integrationCertificateBatchRepository.nextBatch(termination).certificateList().size());
    }
  }

  @Nested
  class CertificateTexts {

    @Test
    void shallGetCertificateTexts() {
      assertEquals(10, integrationCertificateBatchRepository.certificateTexts(termination).size());
    }
  }

  @Nested
  class GetCertificateSummary {

    @Test
    void shallGetCertificateSummary() {
      assertEquals(
          new CertificateSummary(80, 5),
          integrationCertificateBatchRepository.certificateSummary(termination));
    }
  }
}
