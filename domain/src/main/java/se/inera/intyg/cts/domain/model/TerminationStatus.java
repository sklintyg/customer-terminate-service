package se.inera.intyg.cts.domain.model;

/**
 * All steps a Termination can go through.
 */
public enum TerminationStatus {
  //Step 1
  CREATED("Skapad"),
  //Step 2
  COLLECTING_CERTIFICATES("Hämtar intyg"),
  //Step 3
  COLLECTING_CERTIFICATES_COMPLETED("Intyg hämtade"),
  //Step 4
  COLLECTING_CERTIFICATE_TEXTS_COMPLETED("Intygstexter hämtade"),
  //Step 5
  EXPORTED("Uppladdat"),
  //Step 6
  NOTIFICATION_SENT("Notifiering skickad"),
  // Step 6.5 (Optional)
  REMINDER_SENT("Påminnelse skickad"),
  // Step 7
  RECEIPT_RECEIVED("Kvitterad"),
  // Step 8
  PASSWORD_SENT("Kryptonyckel skickad"),
  //Step 8.5 (Optional)
  PASSWORD_RESENT("Kryptonyckel skickad igen"),
  //Step 9
  START_ERASE("Starta radering"),
  //Step 10
  ERASE_IN_PROGRESS("Radering pågår"),
  //Step 10.5 (Optional)
  ERASE_CANCELLED("Radering avbruten"),
  //Step 11
  ERASE_COMPLETED("Radering utförd");

  private final String description;

  TerminationStatus(String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }
}
