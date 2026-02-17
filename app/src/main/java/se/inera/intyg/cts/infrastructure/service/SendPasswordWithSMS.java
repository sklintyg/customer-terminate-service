package se.inera.intyg.cts.infrastructure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.service.SendPassword;
import se.inera.intyg.cts.infrastructure.integration.SendSMS;

@Service
public class SendPasswordWithSMS implements SendPassword {

  private static final Logger LOG = LoggerFactory.getLogger(SendPasswordWithSMS.class);

  private final SendSMS sendSMS;
  private final SmsPhoneNumberFormatter smsPhoneNumberFormatter;

  public SendPasswordWithSMS(SendSMS sendSMS, SmsPhoneNumberFormatter smsPhoneNumberFormatter) {
    this.sendSMS = sendSMS;
    this.smsPhoneNumberFormatter = smsPhoneNumberFormatter;
  }

  @Override
  public boolean sendPassword(Termination termination) {
    final var message = termination.export().password().password();
    final var phoneNumber = termination.export().organizationRepresentative()
        .phoneNumber().number();
    final var formattedPhoneNumber = smsPhoneNumberFormatter.formatPhoneNumber(phoneNumber);

    try {
      final var smsResponseDTO = sendSMS.sendSMS(formattedPhoneNumber, message);
      logSendPasswordSuccess(termination, smsResponseDTO.job_id(), smsResponseDTO.log_href());
      return true;

    } catch (Exception e) {
      logSendPasswordFailure(termination, e);
      return false;
    }
  }

  private void logSendPasswordSuccess(Termination termination, String jobId, String logHref) {
    LOG.info("Successfully sent password with sms for {} with jobId '{}' and logHref '{}'.",
        termination.terminationId().id(), jobId, logHref);
  }

  private void logSendPasswordFailure(Termination termination, Exception e) {
    LOG.error("Failure sending password with sms for {}.", termination.terminationId(), e);
  }
}
