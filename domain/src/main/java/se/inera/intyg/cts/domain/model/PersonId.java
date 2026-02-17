package se.inera.intyg.cts.domain.model;

public record PersonId(String id) {

  public PersonId {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Missing PersonId");
    }
  }
}
