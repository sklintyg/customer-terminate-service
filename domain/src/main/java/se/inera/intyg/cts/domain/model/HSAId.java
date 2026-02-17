package se.inera.intyg.cts.domain.model;

public record HSAId(String id) {

  public HSAId {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Missing HSAId");
    }
  }
}
