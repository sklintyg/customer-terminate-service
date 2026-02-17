package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;

public interface SendPassword {

    boolean sendPassword(Termination termination);

}
