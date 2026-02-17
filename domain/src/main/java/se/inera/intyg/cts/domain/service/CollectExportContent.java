package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;

public interface CollectExportContent {

  void collectCertificates(TerminationId terminationId);

  void collectCertificateTexts(Termination termination);
}
