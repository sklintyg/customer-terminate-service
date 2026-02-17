package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;

public interface SendNotification {

    boolean sendNotification(Termination termination);

    boolean sendReminder(Termination termination);

}
