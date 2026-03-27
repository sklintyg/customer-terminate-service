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
    final var phoneNumber =
        termination.export().organizationRepresentative().phoneNumber().number();
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
    LOG.info(
        "Successfully sent password with sms for {} with jobId '{}' and logHref '{}'.",
        termination.terminationId().id(),
        jobId,
        logHref);
  }

  private void logSendPasswordFailure(Termination termination, Exception e) {
    LOG.error("Failure sending password with sms for {}.", termination.terminationId(), e);
  }
}
