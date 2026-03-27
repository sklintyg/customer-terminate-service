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
package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class CollectExportContentImpl implements CollectExportContent {

  private final TerminationRepository terminationRepository;
  private final CertificateBatchRepository certificateBatchRepository;
  private final CertificateRepository certificateRepository;
  private final CertificateTextRepository certificateTextRepository;

  public CollectExportContentImpl(
      TerminationRepository terminationRepository,
      CertificateBatchRepository certificateBatchRepository,
      CertificateRepository certificateRepository,
      CertificateTextRepository certificateTextRepository) {
    this.terminationRepository = terminationRepository;
    this.certificateBatchRepository = certificateBatchRepository;
    this.certificateRepository = certificateRepository;
    this.certificateTextRepository = certificateTextRepository;
  }

  @Override
  public void collectCertificates(TerminationId terminationId) {
    final var termination = getTermination(terminationId);

    final var certificateBatch = certificateBatchRepository.nextBatch(termination);
    termination.collect(certificateBatch);

    certificateRepository.store(termination, certificateBatch.certificateList());
    terminationRepository.store(termination);
  }

  @Override
  public void collectCertificateTexts(Termination termination) {
    final var certificateTexts = certificateBatchRepository.certificateTexts(termination);
    termination.collect(certificateTexts);

    certificateTextRepository.store(termination, certificateTexts);
    terminationRepository.store(termination);
  }

  private Termination getTermination(TerminationId terminationId) {
    return terminationRepository
        .findByTerminationId(terminationId)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Termination with id: '%s' doesn't exist!".formatted(terminationId)));
  }
}
