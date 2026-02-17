package se.inera.intyg.cts.application.service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.SendPackageNotification;
import se.inera.intyg.cts.domain.service.SendPackagePassword;

@Service
public class MessageServiceImpl implements MessageService {

  private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

  private final TerminationRepository terminationRepository;
  private final SendPackagePassword sendPackagePassword;
  private final SendPackageNotification sendPackageNotification;
  private final Boolean sendPasswordActive;
  private final Boolean sendNotificationActive;
  private final Boolean sendReminderActive;
  private final Integer reminderDelayInMinutes;

  public MessageServiceImpl(TerminationRepository terminationRepository,
      SendPackagePassword sendPackagePassword, SendPackageNotification sendPackageNotification,
      @Value("${send.password.active}") Boolean sendPasswordActive,
      @Value("${send.notification.active}") Boolean sendNotificationActive,
      @Value("${send.reminder.active}") Boolean sendReminderActive,
      @Value("${send.reminder.after.minutes}") Integer reminderDelayInMinutes) {
    this.terminationRepository = terminationRepository;
    this.sendPackagePassword = sendPackagePassword;
    this.sendPackageNotification = sendPackageNotification;
    this.sendPasswordActive = sendPasswordActive;
    this.sendNotificationActive = sendNotificationActive;
    this.sendReminderActive = sendReminderActive;
    this.reminderDelayInMinutes = reminderDelayInMinutes;
  }

  @Override
  public void sendPassword() {
    for (Termination termination : terminationRepository
        .findByStatuses(List.of(TerminationStatus.RECEIPT_RECEIVED))) {

      if (Boolean.TRUE.equals(sendPasswordActive)) {
        sendPassword(termination);

      } else {
        LOG.info("Functionality for sending password is inactive. Not sending password for "
            + "termination id {}", termination.terminationId());
      }
    }
  }

  @Override
  public void sendNotification() {
    for (final var termination : terminationRepository
        .findByStatuses(List.of(TerminationStatus.EXPORTED))) {

      if (sendNotificationActive) {
        sendNotification(termination);

      } else {
        LOG.info("Functionality for sending notification is inactive. Not sending "
            + "notification for termination id {}", termination.terminationId());
      }
    }
  }

  @Override
  public void sendReminder() {
    for (final var termination : terminationRepository
        .findByStatuses(List.of(TerminationStatus.NOTIFICATION_SENT))) {

      if (sendReminderActive && isTimeForReminder(termination)) {
        sendReminder(termination);
      } else if (!sendReminderActive) {
        LOG.info("Functionality for sending reminder is inactive. Not sending "
            + "reminder for termination id {}", termination.terminationId());
      }
    }
  }

  private boolean isTimeForReminder(Termination termination) {
    return termination.export().notificationTime().plusMinutes(reminderDelayInMinutes)
        .isBefore(LocalDateTime.now());
  }

  private void sendPassword(Termination termination) {
    try {
      sendPackagePassword.sendPassword(termination);
    } catch (Exception e) {
      LOG.error("Failure setting status 'password sent' for {}.",
          termination.terminationId(), e);
    }
  }

  private void sendNotification(Termination termination) {
    try {
      sendPackageNotification.sendNotification(termination);

    } catch (Exception e) {
      LOG.error("Failure setting status 'notification sent' for {}.",
          termination.terminationId(), e);
    }
  }

  private void sendReminder(Termination termination) {
    try {
      sendPackageNotification.sendReminder(termination);

    } catch (Exception e) {
      LOG.error("Failure setting status 'reminder sent' for {}.",
          termination.terminationId(), e);
    }
  }
}
