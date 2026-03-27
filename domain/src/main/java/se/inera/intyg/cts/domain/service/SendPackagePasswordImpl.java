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

import java.util.Optional;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class SendPackagePasswordImpl implements SendPackagePassword {

  private final SendPassword sendPassword;
  private final TerminationRepository terminationRepository;

  public SendPackagePasswordImpl(
      SendPassword sendPassword, TerminationRepository terminationRepository) {
    this.sendPassword = sendPassword;
    this.terminationRepository = terminationRepository;
  }

  /**
   * Send the password
   *
   * <p>
   *
   * @param termination to send the password for
   * @return updated termination
   */
  @Override
  public Optional<Termination> sendPassword(Termination termination) {
    final var sendPasswordSuccess = sendPassword.sendPassword(termination);

    if (sendPasswordSuccess) {
      termination.passwordSent();
      terminationRepository.store(termination);
    }
    return terminationRepository.findByTerminationId(termination.terminationId());
  }

  /**
   * Resend the password. This can only be done if the password has been sent at least once.
   *
   * @param termination to resend the password for
   * @return updated termination
   */
  @Override
  public Termination resendPassword(Termination termination) {

    if (termination.status().equals(TerminationStatus.PASSWORD_SENT)
        || termination.status().equals(TerminationStatus.PASSWORD_RESENT)) {
      if (sendPassword.sendPassword(termination)) {
        termination.passwordResent();
        terminationRepository.store(termination);
        return terminationRepository.findByTerminationId(termination.terminationId()).get();
      }
      throw new RuntimeException(
          "Could not store status %s for %s"
              .formatted(TerminationStatus.PASSWORD_RESENT, termination.terminationId().id()));
    }
    throw new IllegalArgumentException(
        String.format("Invalid status: %s to resend password.", termination.status()));
  }
}
