package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;

public interface ExportPackage {

  void export(Termination termination);
}
