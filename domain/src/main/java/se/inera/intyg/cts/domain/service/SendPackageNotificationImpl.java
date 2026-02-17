package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class SendPackageNotificationImpl implements SendPackageNotification {

  private final SendNotification sendNotification;
  private final TerminationRepository terminationRepository;

  public SendPackageNotificationImpl(SendNotification sendNotification,
      TerminationRepository terminationRepository) {
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
