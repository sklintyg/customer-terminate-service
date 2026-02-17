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
      final var formattedPhoneNumber = smsPhoneNumberFormatter.formatPhoneNumber(COMPLIANT_PHONE_NUMBER);
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