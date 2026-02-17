package se.inera.intyg.cts.application.dto;

import se.inera.intyg.cts.domain.model.Termination;

public class TerminationDTOMapper {

  public static TerminationDTO toDTO(Termination termination) {
    return new TerminationDTO(
        termination.terminationId().id(),
        termination.created(),
        termination.creator().hsaId().id(),
        termination.creator().name(),
        termination.status().description(),
        termination.careProvider().hsaId().id(),
        termination.careProvider().organizationNumber().number(),
        termination.export().organizationRepresentative().personId().id(),
        termination.export().organizationRepresentative().phoneNumber().number(),
        termination.export().organizationRepresentative().emailAddress().emailAddress()
    );
  }
}
