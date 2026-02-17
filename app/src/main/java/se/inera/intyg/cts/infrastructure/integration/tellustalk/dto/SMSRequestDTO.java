package se.inera.intyg.cts.infrastructure.integration.tellustalk.dto;

public record SMSRequestDTO(String to, String text, String sms_originator_text) {

  public SMSRequestDTO {
    if (!to.matches("^sms:\\+46[1-9]\\d+$")) {
      throw new IllegalArgumentException(
          String.format("SMS Phone number '%s' format must match 'sms:+46704000000'.", to)
      );
    }
    if (text == null || text.equals("")) {
      throw new IllegalArgumentException("Empty password SMS message is not allowed.");
    }
    if (sms_originator_text.length() > 11) {
      throw new IllegalArgumentException("SMS originator text is longer than 11 characters.");
    }
  }
}
