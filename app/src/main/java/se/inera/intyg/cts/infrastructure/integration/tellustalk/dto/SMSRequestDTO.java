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
package se.inera.intyg.cts.infrastructure.integration.tellustalk.dto;

public record SMSRequestDTO(String to, String text, String sms_originator_text) {

  public SMSRequestDTO {
    if (!to.matches("^sms:\\+46[1-9]\\d+$")) {
      throw new IllegalArgumentException(
          String.format("SMS Phone number '%s' format must match 'sms:+46704000000'.", to));
    }
    if (text == null || text.equals("")) {
      throw new IllegalArgumentException("Empty password SMS message is not allowed.");
    }
    if (sms_originator_text.length() > 11) {
      throw new IllegalArgumentException("SMS originator text is longer than 11 characters.");
    }
  }
}
