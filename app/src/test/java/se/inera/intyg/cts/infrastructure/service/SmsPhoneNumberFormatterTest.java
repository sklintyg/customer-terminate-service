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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SmsPhoneNumberFormatterTest {

  private final SmsPhoneNumberFormatter smsPhoneNumberFormatter = new SmsPhoneNumberFormatter();

  private static final String COMPLIANT_PHONE_NUMBER = "sms:+46701234567";

  @Nested
  class TestPhoneNumberFormatting {

    @Test
    void shouldHandleCompliantPhoneNumber() {
      final var formattedPhoneNumber =
          smsPhoneNumberFormatter.formatPhoneNumber(COMPLIANT_PHONE_NUMBER);
      assertEquals(COMPLIANT_PHONE_NUMBER, formattedPhoneNumber);
    }

    @Test
    void shouldHandlePhoneNumberWithCountryCode() {
      final var formattedPhoneNumber = smsPhoneNumberFormatter.formatPhoneNumber("+46701234567");
      assertEquals(COMPLIANT_PHONE_NUMBER, formattedPhoneNumber);
    }

    @Test
    void shouldHandleStandardPhoneNumberFormat() {
      final var formattedPhoneNumber = smsPhoneNumberFormatter.formatPhoneNumber("070-1234567");
      assertEquals(COMPLIANT_PHONE_NUMBER, formattedPhoneNumber);
    }

    @Test
    void shouldHandleSomeNonStandardPhoneNumberFormat() {
      final var formattedPhoneNumber = smsPhoneNumberFormatter.formatPhoneNumber("+70-123R4 5-67");
      assertEquals(COMPLIANT_PHONE_NUMBER, formattedPhoneNumber);
    }
  }
}
