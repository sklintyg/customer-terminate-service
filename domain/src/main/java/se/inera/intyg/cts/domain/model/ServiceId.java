package se.inera.intyg.cts.domain.model;

public record ServiceId(String id) {

  public ServiceId {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException(
          String.format("ServiceId cannot be null or empty: '%s'", id)
      );
    }
  }
}
