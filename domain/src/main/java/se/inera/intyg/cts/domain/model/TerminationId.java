package se.inera.intyg.cts.domain.model;

import java.util.UUID;

public record TerminationId(UUID id) {

  public TerminationId {
    if (id == null) {
      throw new IllegalArgumentException("Missing TerminationId");
    }
  }
}
