package se.inera.intyg.cts.testability.dto;

import java.util.Collections;
import se.inera.intyg.cts.infrastructure.persistence.entity.ExportEmbeddable;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;

public class TestabilityTerminationDTOMapper {

  public static TerminationEntity toEntity(TestabilityTerminationDTO testabilityTerminationDTO) {
    return new TerminationEntity(
        0L,
        testabilityTerminationDTO.terminationId(),
        testabilityTerminationDTO.created(),
        testabilityTerminationDTO.created(),
        testabilityTerminationDTO.creatorHSAId(),
        testabilityTerminationDTO.creatorName(),
        testabilityTerminationDTO.hsaId(),
        testabilityTerminationDTO.organizationNumber(),
        testabilityTerminationDTO.personId(),
        testabilityTerminationDTO.phoneNumber(),
        testabilityTerminationDTO.emailAddress(),
        testabilityTerminationDTO.status(),
        new ExportEmbeddable(0, 0, null, null, null, null, null),
        Collections.emptyList()
    );
  }
}
