package se.inera.intyg.cts.domain.service;

import java.io.File;
import se.inera.intyg.cts.domain.model.Password;
import se.inera.intyg.cts.domain.model.Termination;

public interface CreatePackage {

  File create(Termination termination, Password password);
}
