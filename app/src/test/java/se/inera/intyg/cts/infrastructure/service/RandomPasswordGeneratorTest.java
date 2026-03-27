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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RandomPasswordGeneratorTest {

  @InjectMocks RandomPasswordGenerator randomPasswordGenerator;

  @Test
  void testGenerateSecureRandomPasswordChar() {
    String password = randomPasswordGenerator.generateSecurePassword();
    int specialCharCount = 0;
    int numberCharCount = 0;
    int upperCharCount = 0;
    int lowerCharCount = 0;

    for (char c : password.toCharArray()) {
      if (c >= 33 && c <= 47) {
        specialCharCount++;
      } else if (c >= 48 && c <= 57) {
        numberCharCount++;
      } else if (c >= 65 && c <= 90) {
        upperCharCount++;
      } else if (c >= 97 && c <= 122) {
        lowerCharCount++;
      }
    }

    assertEquals(
        2, specialCharCount, "Password validation failed wrong number of special characters.");
    assertEquals(2, numberCharCount, "Password validation failed wrong number of numbers.");
    assertEquals(
        2, upperCharCount, "Password validation failed wrong number of upper case characters.");
    assertEquals(
        4, lowerCharCount, "Password validation failed wrong number of lower case characters.");
  }

  @Test
  void testPasswordNotEqual() {
    String password1 = randomPasswordGenerator.generateSecurePassword();
    String password2 = randomPasswordGenerator.generateSecurePassword();
    assertNotEquals(password1, password2, "Password validation failed, passwords are identical");
  }
}
