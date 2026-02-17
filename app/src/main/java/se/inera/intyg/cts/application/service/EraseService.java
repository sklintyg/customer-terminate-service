package se.inera.intyg.cts.application.service;

import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.domain.model.TerminationId;

public interface EraseService {

  void erase();

  TerminationDTO initiateErase(TerminationId terminationId);
}
