package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.EmailAddress;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.PersonId;
import se.inera.intyg.cts.domain.model.PhoneNumber;
import se.inera.intyg.cts.domain.model.Termination;

public interface UpdateTermination {

  Termination update(Termination termination, HSAId hsaId, PersonId personId,
      EmailAddress emailAddress, PhoneNumber phoneNumber);
}
