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

  @Mock
  private SendNotification sendNotification;

  private SendPackageNotification sendPackageNotification;
  private InMemoryTerminationRepository terminationRepository;

  @BeforeEach
  void setUp() {
    terminationRepository = new InMemoryTerminationRepository();
    sendPackageNotification = new SendPackageNotificationImpl(sendNotification,
        terminationRepository);
  }

  @Test
  public void shouldUpdateTerminationWhenSuccessfulNotification() {
    final var termination = createTermination(TerminationStatus.EXPORTED);
    doReturn(true).when(sendNotification).sendNotification(termination);

    sendPackageNotification.sendNotification(termination);

    assertEquals(TerminationStatus.NOTIFICATION_SENT,
        termination(termination.terminationId()).status());
  }

  @Test
  public void shouldUpdateExportTimeWhenSuccessfulNotification() {
    final var termination = createTermination(TerminationStatus.EXPORTED);
    doReturn(true).when(sendNotification).sendNotification(termination);

    final var beforeDateTime = LocalDateTime.now();
    sendPackageNotification.sendNotification(termination);

    final var export = termination(termination.terminationId()).export();
    assertFalse(export.notificationTime().isBefore(beforeDateTime), () ->
        String.format("Expect notificationTime '%s' to be updated and not before '%s'",
            export.notificationTime(), beforeDateTime)
    );
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

    assertEquals(TerminationStatus.REMINDER_SENT,
        termination(termination.terminationId()).status());
  }

  @Test
  public void shouldUpdateReminderTimeWhenSuccessfulReminder() {
    final var termination = createTermination(TerminationStatus.NOTIFICATION_SENT);
    doReturn(true).when(sendNotification).sendReminder(termination);

    final var beforeDateTime = LocalDateTime.now();
    sendPackageNotification.sendReminder(termination);

    final var export = termination(termination.terminationId()).export();
    assertFalse(export.reminderTime().isBefore(beforeDateTime), () ->
        String.format("Expect reminderTime '%s' to be updated and not before '%s'",
            export.reminderTime(), beforeDateTime)
    );
  }

  @Test
  public void shouldNotUpdateTerminationWhenFailedReminder() {
    final var termination = createTermination(TerminationStatus.NOTIFICATION_SENT);
    doReturn(false).when(sendNotification).sendNotification(termination);

    sendPackageNotification.sendNotification(termination);

    assertEquals(TerminationStatus.NOTIFICATION_SENT,
        termination(termination.terminationId()).status());
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
