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

import se.inera.intyg.cts.domain.model.EmailAddress;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.PersonId;
import se.inera.intyg.cts.domain.model.PhoneNumber;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class UpdateTerminationImpl implements UpdateTermination {

  private final TerminationRepository terminationRepository;
  private final CertificateRepository certificateRepository;
  private final CertificateTextRepository certificateTextRepository;

  public UpdateTerminationImpl(
      TerminationRepository terminationRepository,
      CertificateRepository certificateRepository,
      CertificateTextRepository certificateTextRepository) {
    this.terminationRepository = terminationRepository;
    this.certificateRepository = certificateRepository;
    this.certificateTextRepository = certificateTextRepository;
  }

  /**
   * Updates the termination. If the termination´s status been reset to collect certificates, and
   * its texts, any already collected data will be removed.
   */
  @Override
  public Termination update(
      Termination termination,
      HSAId hsaId,
      PersonId personId,
      EmailAddress emailAddress,
      PhoneNumber phoneNumber) {

    termination.update(hsaId, personId, emailAddress, phoneNumber);

    final var storeTermination = terminationRepository.store(termination);
    if (storeTermination.status() == TerminationStatus.CREATED) {
      certificateRepository.remove(storeTermination);
      certificateTextRepository.remove(storeTermination);
    }

    return storeTermination;
  }
}
