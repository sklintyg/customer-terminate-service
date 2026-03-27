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
package se.inera.intyg.cts.infrastructure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.service.SendNotification;
import se.inera.intyg.cts.infrastructure.integration.SendEmail;
import se.inera.intyg.cts.infrastructure.integration.SendSMS;

@Service
public class SendPackageNotification implements SendNotification {

  private static final Logger LOG = LoggerFactory.getLogger(SendPackageNotification.class);

  @Value("${message.notification.email.content}")
  private String notificationEmailContent;

  @Value("${message.notification.sms.content}")
  private String notificationSmsContent;

  @Value("${message.reminder.email.content}")
  private String reminderEmailContent;

  @Value("${message.reminder.sms.content}")
  private String reminderSmsContent;

  @Value("${message.notification.email.subject}")
  private String notificationSubject;

  @Value("${message.reminder.email.subject}")
  private String reminderSubject;

  private static final String SMS = "sms";
  private static final String EMAIL = "email";
  private static final String REMINDER = "reminder";
  private static final String NOTIFICATION = "notification";
  private static final String EMAIL_REGEX =
      "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

  private final SendSMS sendSMS;
  private final SendEmail sendEmail;
  private final SmsPhoneNumberFormatter smsPhoneNumberFormatter;

  public SendPackageNotification(
      SendSMS sendSMS, SendEmail sendEmail, SmsPhoneNumberFormatter smsPhoneNumberFormatter) {
    this.sendSMS = sendSMS;
    this.sendEmail = sendEmail;
    this.smsPhoneNumberFormatter = smsPhoneNumberFormatter;
  }

  @Override
  public boolean sendNotification(Termination termination) {
    final var smsSuccess = sendSms(notificationSmsContent, NOTIFICATION, termination);
    final var emailSuccess =
        sendEmail(notificationEmailContent, NOTIFICATION, notificationSubject, termination);

    return smsSuccess || emailSuccess;
  }

  @Override
  public boolean sendReminder(Termination termination) {
    final var smsSuccess = sendSms(reminderSmsContent, REMINDER, termination);
    final var emailSuccess =
        sendEmail(reminderEmailContent, REMINDER, reminderSubject, termination);

    return smsSuccess || emailSuccess;
  }

  private boolean sendSms(String message, String statusType, Termination termination) {
    try {
      final var phoneNumber =
          termination.export().organizationRepresentative().phoneNumber().number();
      final var formattedPhoneNumber = smsPhoneNumberFormatter.formatPhoneNumber(phoneNumber);
      final var smsResponseDTO = sendSMS.sendSMS(formattedPhoneNumber, message);
      logSendSmsSuccess(
          statusType,
          termination.terminationId(),
          smsResponseDTO.job_id(),
          smsResponseDTO.log_href());
      return true;

    } catch (Exception e) {
      logSendMessageFailure(SMS, statusType, termination.terminationId(), e);
      return false;
    }
  }

  private boolean sendEmail(
      String message, String statusType, String subject, Termination termination) {
    try {
      final var emailAddress =
          termination.export().organizationRepresentative().emailAddress().emailAddress();

      if (invalidEmailAddress(emailAddress)) {
        LOG.error(
            "Failure sending email {} for {}. Email address has invalid format.",
            statusType,
            termination);
        return false;
      }

      sendEmail.sendEmail(emailAddress, message, subject);
      logSendEmailSuccess(statusType, termination.terminationId());
      return true;

    } catch (Exception e) {
      logSendMessageFailure(EMAIL, statusType, termination.terminationId(), e);
      return false;
    }
  }

  private boolean invalidEmailAddress(String emailAddress) {
    return !emailAddress.matches(EMAIL_REGEX);
  }

  private void logSendSmsSuccess(
      String statusType, TerminationId terminationId, String jobId, String logHref) {
    LOG.info(
        "Successfully sent sms {} for {} with jobId '{}' and logHref '{}'.",
        statusType,
        terminationId.id(),
        jobId,
        logHref);
  }

  private void logSendEmailSuccess(String statusType, TerminationId terminationId) {
    LOG.info("Successfully sent email {} for {}.", statusType, terminationId.id());
  }

  private void logSendMessageFailure(
      String messageType, String statusType, TerminationId terminationId, Exception e) {
    LOG.error("Failure sending {} {} for {}.", messageType, statusType, terminationId.id(), e);
  }
}
