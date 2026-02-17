package se.inera.intyg.cts.domain.model;

public record OrganizationNumber(String number) {

  public OrganizationNumber {
    if (number == null || number.isBlank()) {
      throw new IllegalArgumentException("Missing OrganizationNumber");
    }
  }
}
