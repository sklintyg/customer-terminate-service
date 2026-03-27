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
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class SendPackageNotificationImpl implements SendPackageNotification {

  private final SendNotification sendNotification;
  private final TerminationRepository terminationRepository;

  public SendPackageNotificationImpl(
      SendNotification sendNotification, TerminationRepository terminationRepository) {
    this.sendNotification = sendNotification;
    this.terminationRepository = terminationRepository;
  }

  @Override
  public void sendNotification(Termination termination) {
    final var sendNotificationSuccess = sendNotification.sendNotification(termination);

    if (sendNotificationSuccess) {
      termination.notificationSent();
      terminationRepository.store(termination);
    }
  }

  @Override
  public void sendReminder(Termination termination) {
    final var sendReminderSuccess = sendNotification.sendReminder(termination);

    if (sendReminderSuccess) {
      termination.reminderSent();
      terminationRepository.store(termination);
    }
  }
}
