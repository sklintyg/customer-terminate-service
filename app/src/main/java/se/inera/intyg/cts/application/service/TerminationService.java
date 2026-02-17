package se.inera.intyg.cts.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import se.inera.intyg.cts.application.dto.CreateTerminationDTO;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.application.dto.UpdateTerminationDTO;

public interface TerminationService {

    TerminationDTO create(CreateTerminationDTO createTerminationDTO);

    Optional<TerminationDTO> findById(UUID terminationId);

    List<TerminationDTO> findAll();

    TerminationDTO resendPassword(UUID terminationId);

    TerminationDTO update(UUID terminationId, UpdateTerminationDTO updateTerminationDTO);
}
