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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.terminationWithPhoneNumber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.infrastructure.integration.SendSMS;
import se.inera.intyg.cts.infrastructure.integration.tellustalk.dto.TellusTalkResponseDTO;

@ExtendWith(MockitoExtension.class)
class SendPasswordWithSMSTest {

  @Mock private SendSMS sendSMS;
  @Mock private SmsPhoneNumberFormatter smsPhoneNumberFormatter;

  @InjectMocks private SendPasswordWithSMS smsService;

  private static final String COMPLIANT_PHONE_NUMBER = "sms:+46701234567";

  @Test
  public void shouldReturnTrueWhenPasswordSmsSentSuccessfully() {
    final var termination = terminationWithPhoneNumber(COMPLIANT_PHONE_NUMBER);
    final var orgRep = termination.export().organizationRepresentative();
    setMocksForSuccess();

    final var response = smsService.sendPassword(termination);

    verify(smsPhoneNumberFormatter, times(1)).formatPhoneNumber(orgRep.phoneNumber().number());
    verify(sendSMS, times(1))
        .sendSMS(orgRep.phoneNumber().number(), termination.export().password().password());
    assertTrue(response);
  }

  @Test
  public void shouldReturnFalseWhenPasswordSmsFailure() {
    final var termination = terminationWithPhoneNumber(COMPLIANT_PHONE_NUMBER);
    final var orgRep = termination.export().organizationRepresentative();
    setMocksForFailure();

    final var response = smsService.sendPassword(termination);

    verify(smsPhoneNumberFormatter, times(1)).formatPhoneNumber(orgRep.phoneNumber().number());
    verify(sendSMS, times(1))
        .sendSMS(orgRep.phoneNumber().number(), termination.export().password().password());
    assertFalse(response);
  }

  private void setMocksForSuccess() {
    when(smsPhoneNumberFormatter.formatPhoneNumber(any(String.class)))
        .thenReturn(COMPLIANT_PHONE_NUMBER);
    when(sendSMS.sendSMS(any(String.class), any(String.class)))
        .thenReturn(new TellusTalkResponseDTO("ID", "URL"));
  }

  private void setMocksForFailure() {
    when(smsPhoneNumberFormatter.formatPhoneNumber(any(String.class)))
        .thenReturn(COMPLIANT_PHONE_NUMBER);
    doThrow(new RuntimeException()).when(sendSMS).sendSMS(any(String.class), any(String.class));
  }
}
