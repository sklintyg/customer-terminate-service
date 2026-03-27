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
package se.inera.intyg.cts.testutil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationBuilder;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.infrastructure.persistence.entity.ExportEmbeddable;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;

public class TerminationTestDataBuilder {

  public static final UUID DEFAULT_TERMINATION_ID = UUID.randomUUID();
  public static final LocalDateTime DEFAULT_CREATED = LocalDateTime.now();
  public static final LocalDateTime DEFAULT_MODIFIED = LocalDateTime.now();
  public static final String DEFAULT_HSA_ID = "TSTNMT2321000156-ALFA";
  public static final String DEFAULT_CREATOR_HSA_ID = "creatorHSAId";
  public static final String DEFAULT_CREATOR_NAME = "creatorName";
  public static final String DEFAULT_ORGANIZATION_NUMBER = "2-orgnr-ALFA";
  public static final String DEFAULT_PERSON_ID = "191212121212";
  public static final String DEFAULT_PHONE_NUMBER = "phoneNumber";
  public static final String DEFAULT_EMAIL_ADDRESS = "email@address.se";

  public static final String DEFAULT_PASSWORD = "Password";
  public static final TerminationStatus DEFAULT_STATUS = TerminationStatus.CREATED;

  public static Termination defaultTermination() {
    return defaultTerminationBuilder().create();
  }

  public static TerminationBuilder defaultTerminationBuilder() {
    return TerminationBuilder.getInstance()
        .created(DEFAULT_CREATED)
        .modified(DEFAULT_MODIFIED)
        .creatorHSAId(DEFAULT_CREATOR_HSA_ID)
        .creatorName(DEFAULT_CREATOR_NAME)
        .careProviderHSAId(DEFAULT_HSA_ID)
        .careProviderOrganizationNumber(DEFAULT_ORGANIZATION_NUMBER)
        .careProviderOrganizationRepresentativePersonId(DEFAULT_PERSON_ID)
        .careProviderOrganizationRepresentativePhoneNumber(DEFAULT_PHONE_NUMBER)
        .careProviderOrganizationRepresentativeEmailAddress(DEFAULT_EMAIL_ADDRESS)
        .status(TerminationStatus.CREATED)
        .packagePassword(DEFAULT_PASSWORD);
  }

  public static TerminationDTO defaultTerminationDTO() {
    return new TerminationDTO(
        DEFAULT_TERMINATION_ID,
        DEFAULT_CREATED,
        DEFAULT_CREATOR_HSA_ID,
        DEFAULT_CREATOR_NAME,
        DEFAULT_STATUS.toString(),
        DEFAULT_HSA_ID,
        DEFAULT_ORGANIZATION_NUMBER,
        DEFAULT_PERSON_ID,
        DEFAULT_PHONE_NUMBER,
        DEFAULT_EMAIL_ADDRESS);
  }

  public static TerminationEntity defaultTerminationEntity() {
    return new TerminationEntity(
        null,
        DEFAULT_TERMINATION_ID,
        DEFAULT_CREATED,
        DEFAULT_MODIFIED,
        DEFAULT_CREATOR_HSA_ID,
        DEFAULT_CREATOR_NAME,
        DEFAULT_HSA_ID,
        DEFAULT_ORGANIZATION_NUMBER,
        DEFAULT_PERSON_ID,
        DEFAULT_PHONE_NUMBER,
        DEFAULT_EMAIL_ADDRESS,
        DEFAULT_STATUS.toString(),
        new ExportEmbeddable(0, 0, null, null, null, null, null),
        Collections.emptyList());
  }

  public static TerminationEntity defaultTerminationEntity(UUID terminationId) {
    return new TerminationEntity(
        null,
        terminationId,
        DEFAULT_CREATED,
        DEFAULT_MODIFIED,
        DEFAULT_CREATOR_HSA_ID,
        DEFAULT_CREATOR_NAME,
        DEFAULT_HSA_ID,
        DEFAULT_ORGANIZATION_NUMBER,
        DEFAULT_PERSON_ID,
        DEFAULT_PHONE_NUMBER,
        DEFAULT_EMAIL_ADDRESS,
        DEFAULT_STATUS.toString(),
        new ExportEmbeddable(0, 0, null, null, null, null, null),
        Collections.emptyList());
  }

  public static Termination terminationWithPhoneNumber(String phoneNumber) {
    return defaultTerminationBuilder()
        .careProviderOrganizationRepresentativePhoneNumber(phoneNumber)
        .create();
  }

  public static Termination terminationWithNotificationTime(LocalDateTime notificationTime) {
    return defaultTerminationBuilder().notificationTime(notificationTime).create();
  }

  public static Termination terminationWithEmailAddress(String emailAddress) {
    return defaultTerminationBuilder()
        .careProviderOrganizationRepresentativeEmailAddress(emailAddress)
        .create();
  }
}
