package se.inera.intyg.cts.application.service;

import java.util.UUID;

public interface ReceiptService {

  void handleReceipt(UUID terminationUUID);

}
