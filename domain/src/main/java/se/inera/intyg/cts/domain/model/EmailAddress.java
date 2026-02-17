package se.inera.intyg.cts.domain.model;

public record EmailAddress(String emailAddress) {

  public EmailAddress {
    if (emailAddress == null || emailAddress.isBlank()) {
      throw new IllegalArgumentException("Missing EmailAddress");
    }
  }
}
