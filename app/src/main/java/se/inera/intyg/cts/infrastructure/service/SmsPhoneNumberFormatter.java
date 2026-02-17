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
