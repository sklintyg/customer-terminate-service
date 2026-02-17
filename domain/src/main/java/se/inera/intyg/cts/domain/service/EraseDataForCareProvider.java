package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;

public interface EraseDataForCareProvider {

  void erase(Termination termination);
}
