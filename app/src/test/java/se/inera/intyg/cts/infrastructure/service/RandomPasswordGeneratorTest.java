package se.inera.intyg.cts.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RandomPasswordGeneratorTest {

  @InjectMocks
  RandomPasswordGenerator randomPasswordGenerator;

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

    assertEquals(2, specialCharCount,
        "Password validation failed wrong number of special characters.");
    assertEquals(2, numberCharCount, "Password validation failed wrong number of numbers.");
    assertEquals(2, upperCharCount,
        "Password validation failed wrong number of upper case characters.");
    assertEquals(4, lowerCharCount,
        "Password validation failed wrong number of lower case characters.");
  }

  @Test
  void testPasswordNotEqual() {
    String password1 = randomPasswordGenerator.generateSecurePassword();
    String password2 = randomPasswordGenerator.generateSecurePassword();
    assertNotEquals(password1, password2, "Password validation failed, passwords are identical");
  }
}