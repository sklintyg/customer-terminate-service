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

import org.springframework.stereotype.Service;

@Service
public class SmsPhoneNumberFormatter {

  private static final String SMS_PREFIX = "sms:";
  private static final String COUNTRY_CODE = "+46";

  public String formatPhoneNumber(String phoneNumber) {

    if (phoneNumber.matches("^sms:\\+46[1-9]\\d+$")) {
      return phoneNumber;
    }

    if (phoneNumber.matches("^\\+46[1-9]\\d+$")) {
      return SMS_PREFIX + phoneNumber;
    }

    String cleanedPhoneNumber = phoneNumber.replaceAll("\\D", "");
    if (phoneNumber.startsWith("0")) {
      cleanedPhoneNumber = cleanedPhoneNumber.substring(1);
    }

    return SMS_PREFIX + COUNTRY_CODE + cleanedPhoneNumber;
  }
}
