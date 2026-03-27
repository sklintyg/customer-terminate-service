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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.terminationWithStatus;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.InMemoryTerminationRepository;

@ExtendWith(MockitoExtension.class)
class SendPackageNotificationTest {

  @Mock private SendNotification sendNotification;

  private SendPackageNotification sendPackageNotification;
  private InMemoryTerminationRepository terminationRepository;

  @BeforeEach
  void setUp() {
    terminationRepository = new InMemoryTerminationRepository();
    sendPackageNotification =
        new SendPackageNotificationImpl(sendNotification, terminationRepository);
  }

  @Test
  public void shouldUpdateTerminationWhenSuccessfulNotification() {
    final var termination = createTermination(TerminationStatus.EXPORTED);
    doReturn(true).when(sendNotification).sendNotification(termination);

    sendPackageNotification.sendNotification(termination);

    assertEquals(
        TerminationStatus.NOTIFICATION_SENT, termination(termination.terminationId()).status());
  }

  @Test
  public void shouldUpdateExportTimeWhenSuccessfulNotification() {
    final var termination = createTermination(TerminationStatus.EXPORTED);
    doReturn(true).when(sendNotification).sendNotification(termination);

    final var beforeDateTime = LocalDateTime.now();
    sendPackageNotification.sendNotification(termination);

    final var export = termination(termination.terminationId()).export();
    assertFalse(
        export.notificationTime().isBefore(beforeDateTime),
        () ->
            String.format(
                "Expect notificationTime '%s' to be updated and not before '%s'",
                export.notificationTime(), beforeDateTime));
  }

  @Test
  public void shouldNotUpdateTerminationWhenFailedNotification() {
    final var termination = createTermination(TerminationStatus.EXPORTED);
    doReturn(false).when(sendNotification).sendNotification(termination);

    sendPackageNotification.sendNotification(termination);

    assertEquals(TerminationStatus.EXPORTED, termination(termination.terminationId()).status());
  }

  @Test
  public void shouldUpdateTerminationWhenSuccessfulReminder() {
    final var termination = createTermination(TerminationStatus.NOTIFICATION_SENT);
    doReturn(true).when(sendNotification).sendReminder(termination);

    sendPackageNotification.sendReminder(termination);

    assertEquals(
        TerminationStatus.REMINDER_SENT, termination(termination.terminationId()).status());
  }

  @Test
  public void shouldUpdateReminderTimeWhenSuccessfulReminder() {
    final var termination = createTermination(TerminationStatus.NOTIFICATION_SENT);
    doReturn(true).when(sendNotification).sendReminder(termination);

    final var beforeDateTime = LocalDateTime.now();
    sendPackageNotification.sendReminder(termination);

    final var export = termination(termination.terminationId()).export();
    assertFalse(
        export.reminderTime().isBefore(beforeDateTime),
        () ->
            String.format(
                "Expect reminderTime '%s' to be updated and not before '%s'",
                export.reminderTime(), beforeDateTime));
  }

  @Test
  public void shouldNotUpdateTerminationWhenFailedReminder() {
    final var termination = createTermination(TerminationStatus.NOTIFICATION_SENT);
    doReturn(false).when(sendNotification).sendNotification(termination);

    sendPackageNotification.sendNotification(termination);

    assertEquals(
        TerminationStatus.NOTIFICATION_SENT, termination(termination.terminationId()).status());
  }

  private Termination createTermination(TerminationStatus status) {
    final var termination = terminationWithStatus(status);
    terminationRepository.store(termination);
    return termination;
  }

  private Termination termination(TerminationId terminationId) {
    return terminationRepository.findByTerminationId(terminationId).orElseThrow();
  }
}
