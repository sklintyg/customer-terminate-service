package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.Termination;

public interface SendPackageNotification {

  void sendNotification(Termination termination);

  void sendReminder(Termination termination);
}
