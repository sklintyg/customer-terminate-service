package se.inera.intyg.cts.domain.model;

public record PhoneNumber(String number) {

  public PhoneNumber {
    if (number == null || number.isBlank()) {
      throw new IllegalArgumentException("Missing PhoneNumber");
    }
  }
}
