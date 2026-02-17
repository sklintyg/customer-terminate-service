package se.inera.intyg.cts.domain.model;

public record Staff(HSAId hsaId, String name) {

  public Staff {
    if (hsaId == null) {
      throw new IllegalArgumentException("Missing HSAId");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Missing Name");
    }
  }
}
