package se.inera.intyg.cts.domain.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import se.inera.intyg.cts.domain.model.EraseService;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationBuilder;
import se.inera.intyg.cts.domain.model.TerminationStatus;

public class TerminationTestDataFactory {

  public static final UUID DEFAULT_TERMINATION_ID = UUID.randomUUID();
  public static final LocalDateTime DEFAULT_CREATED = LocalDateTime.now();
  public static final LocalDateTime DEFAULT_MODIFIED = LocalDateTime.now();
  public static final String DEFAULT_HSA_ID = "hsaId";
  public static final String DEFAULT_CREATOR_HSA_ID = "creatorHSAId";
  public static final String DEFAULT_CREATOR_NAME = "creatorName";
  public static final String DEFAULT_ORGANIZATIONAL_NUMBER = "organizationalNumber";
  public static final String DEFAULT_PERSON_ID = "personId";
  public static final String DEFAULT_PHONE_NUMBER = "phoneNumber";
  public static final String DEFAULT_EMAIL_ADDRESS = "email@address.se";
  public static final TerminationStatus DEFAULT_STATUS = TerminationStatus.CREATED;
  private static final String DEFAULT_PASSWORD = "Password";
  public static final int DEFAULT_TOTAL = 100;
  public static final int DEFAULT_REVOKED = 10;
  private static final LocalDateTime DEFAULT_RECEIPT_TIME = DEFAULT_CREATED.plus(10,
      ChronoUnit.DAYS);

  public static Termination defaultTermination() {
    return defaultTerminationBuilder()
        .create();
  }

  public static Termination exportedTerminationWithStartErase() {
    return defaultTerminationBuilder()
        .status(TerminationStatus.START_ERASE)
        .packagePassword(DEFAULT_PASSWORD)
        .total(DEFAULT_TOTAL)
        .revoked(DEFAULT_REVOKED)
        .receiptTime(DEFAULT_RECEIPT_TIME)
        .create();
  }

  public static Termination exportedTerminationWithEraseInProgress(
      List<EraseService> eraseServices) {
    return defaultTerminationBuilder()
        .status(TerminationStatus.ERASE_IN_PROGRESS)
        .packagePassword(DEFAULT_PASSWORD)
        .total(DEFAULT_TOTAL)
        .revoked(DEFAULT_REVOKED)
        .receiptTime(DEFAULT_RECEIPT_TIME)
        .eraseServices(eraseServices)
        .create();
  }

  public static Termination terminationWithStatus(TerminationStatus status) {
    return defaultTerminationBuilder()
        .status(status)
        .create();
  }

  public static TerminationBuilder defaultTerminationBuilder() {
    return TerminationBuilder.getInstance()
        .terminationId(DEFAULT_TERMINATION_ID)
        .created(DEFAULT_CREATED)
        .modified(DEFAULT_MODIFIED)
        .creatorHSAId(DEFAULT_CREATOR_HSA_ID)
        .creatorName(DEFAULT_CREATOR_NAME)
        .careProviderHSAId(DEFAULT_HSA_ID)
        .careProviderOrganizationNumber(DEFAULT_ORGANIZATIONAL_NUMBER)
        .careProviderOrganizationRepresentativePersonId(DEFAULT_PERSON_ID)
        .careProviderOrganizationRepresentativePhoneNumber(DEFAULT_PHONE_NUMBER)
        .careProviderOrganizationRepresentativeEmailAddress(DEFAULT_EMAIL_ADDRESS)
        .status(DEFAULT_STATUS);
  }
}
