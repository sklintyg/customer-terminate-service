package se.inera.intyg.cts.domain.service;

import java.util.Optional;
import se.inera.intyg.cts.domain.model.Termination;

public interface SendPackagePassword {

  Optional<Termination> sendPassword(Termination termination);

  Termination resendPassword(Termination termination);
}
