/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.domain.model;

/** All steps a Termination can go through. */
public enum TerminationStatus {
  // Step 1
  CREATED("Skapad"),
  // Step 2
  COLLECTING_CERTIFICATES("Hämtar intyg"),
  // Step 3
  COLLECTING_CERTIFICATES_COMPLETED("Intyg hämtade"),
  // Step 4
  COLLECTING_CERTIFICATE_TEXTS_COMPLETED("Intygstexter hämtade"),
  // Step 5
  EXPORTED("Uppladdat"),
  // Step 6
  NOTIFICATION_SENT("Notifiering skickad"),
  // Step 6.5 (Optional)
  REMINDER_SENT("Påminnelse skickad"),
  // Step 7
  RECEIPT_RECEIVED("Kvitterad"),
  // Step 8
  PASSWORD_SENT("Kryptonyckel skickad"),
  // Step 8.5 (Optional)
  PASSWORD_RESENT("Kryptonyckel skickad igen"),
  // Step 9
  START_ERASE("Starta radering"),
  // Step 10
  ERASE_IN_PROGRESS("Radering pågår"),
  // Step 10.5 (Optional)
  ERASE_CANCELLED("Radering avbruten"),
  // Step 11
  ERASE_COMPLETED("Radering utförd");

  private final String description;

  TerminationStatus(String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }
}
