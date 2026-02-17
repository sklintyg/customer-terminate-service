package se.inera.intyg.cts.testability.dto;

import se.inera.intyg.cts.domain.model.Password;
import se.inera.intyg.cts.infrastructure.persistence.entity.ExportEmbeddable;

public class TestabilityExportEmbeddableDTOMapper {

  public static TestabilityExportEmbeddableDTO toDomain(ExportEmbeddable exportEmbeddable) {
    return new TestabilityExportEmbeddableDTO(
        exportEmbeddable.getTotal(),
        exportEmbeddable.getRevoked(),
        new Password(exportEmbeddable.getPassword()),
        exportEmbeddable.getReceiptTime());
  }
}
