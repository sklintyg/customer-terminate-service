package se.inera.intyg.cts.domain.model;

import java.util.List;

public record Erase(List<EraseService> eraseServices) {

  public Erase {
    if (eraseServices == null) {
      throw new IllegalArgumentException("EraseServices cannot be null!");
    }
  }
}
