package se.inera.intyg.cts.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TerminationBuilderTest {

  public static final UUID DEFAULT_TERMINATION_ID = UUID.randomUUID();
  public static final LocalDateTime DEFAULT_CREATED = LocalDateTime.now();
  public static final LocalDateTime DEFAULT_MODIFIED = LocalDateTime.now();
  public static final String HSA_ID = "hsaId";
  public static final String CREATOR_HSA_ID = "creatorHSAId";
  public static final String CREATOR_NAME = "creatorName";
  public static final String ORGANIZATION_NUMBER = "organizationNumber";
  public static final String PERSON_ID = "personId";
  public static final String PHONE_NUMBER = "phoneNumber";
  public static final String EMAIL_ADDRESS = "email@address.se";
  public static final int TOTAL = 145;
  public static final int REVOKED = 13;
  public static final String PASSWORD = "thisisapassword";
  public static final TerminationStatus DEFAULT_STATUS = TerminationStatus.CREATED;

  @Nested
  class CreateNewTermination {

    @Test
    void shallCreateTerminationWithTerminationId() {
      assertNotNull(terminationBuilder().create().terminationId(),
          "Expect Termination to have an id");
    }

    @Test
    void shallCreateTerminationWithCreated() {
      assertNotNull(terminationBuilder().create().created(), "Expect Termination to have created");
    }

    @Test
    void shallCreateTerminationWithStatusCreated() {
      assertEquals(TerminationStatus.CREATED, terminationBuilder().create().status());
    }
  }

  @Nested
  class CreateExistingTermination {

    @Test
    void shallCreateTerminationWithTerminationId() {
      assertEquals(DEFAULT_TERMINATION_ID,
          terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .created(DEFAULT_CREATED)
              .modified(DEFAULT_MODIFIED)
              .status(DEFAULT_STATUS)
              .create().terminationId().id());
    }

    @Test
    void shallCreateTerminationWithCreated() {
      assertEquals(DEFAULT_CREATED,
          terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .created(DEFAULT_CREATED)
              .modified(DEFAULT_MODIFIED)
              .status(DEFAULT_STATUS)
              .create().created());
    }

    @Test
    void shallCreateTerminationWithModified() {
      assertEquals(DEFAULT_MODIFIED,
          terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .created(DEFAULT_CREATED)
              .modified(DEFAULT_MODIFIED)
              .status(DEFAULT_STATUS)
              .create().modified());
    }

    @Test
    void shallCreateTerminationWithStatusCreated() {
      assertEquals(DEFAULT_STATUS,
          terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .created(DEFAULT_CREATED)
              .modified(DEFAULT_MODIFIED)
              .status(DEFAULT_STATUS)
              .create().status());
    }

    @Test
    void shallNotAcceptExistingTerminationWithoutCreated() {
      final var exception = assertThrows(IllegalArgumentException.class,
          () -> terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .status(DEFAULT_STATUS)
              .create());
      assertEquals("Missing Created", exception.getMessage());
    }

    @Test
    void shallNotAcceptExistingTerminationWithoutModified() {
      final var exception = assertThrows(IllegalArgumentException.class,
          () -> terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .created(DEFAULT_CREATED)
              .status(DEFAULT_STATUS)
              .create());
      assertEquals("Missing Modified", exception.getMessage());
    }

    @Test
    void shallNotAcceptExistingTerminationWithoutStatus() {
      final var exception = assertThrows(IllegalArgumentException.class,
          () -> terminationBuilder()
              .terminationId(DEFAULT_TERMINATION_ID)
              .created(DEFAULT_CREATED)
              .modified(DEFAULT_MODIFIED)
              .create());
      assertEquals("Missing Status", exception.getMessage());
    }
  }

  @Test
  void shallCreateTerminationWithHSAId() {
    assertEquals(HSA_ID, terminationBuilder().create().careProvider().hsaId().id());
  }

  @Test
  void shallCreateTerminationWithOrganizationNumber() {
    assertEquals(ORGANIZATION_NUMBER,
        terminationBuilder().create().careProvider().organizationNumber().number());
  }

  @Test
  void shallCreateTerminationWithPersonId() {
    assertEquals(PERSON_ID,
        terminationBuilder().create().export().organizationRepresentative().personId().id());
  }

  @Test
  void shallCreateTerminationWithPhoneNumber() {
    assertEquals(PHONE_NUMBER,
        terminationBuilder().create().export().organizationRepresentative().phoneNumber()
            .number());
  }

  @Test
  void shallCreateTerminationWithEmailAddress() {
    assertEquals(EMAIL_ADDRESS,
        terminationBuilder().create().export().organizationRepresentative().emailAddress()
            .emailAddress());
  }

  @Test
  void shallCreateTerminationWithTotal() {
    assertEquals(TOTAL,
        terminationBuilder().create().export().certificateSummary().total());
  }

  @Test
  void shallCreateTerminationWithRevoked() {
    assertEquals(REVOKED,
        terminationBuilder().create().export().certificateSummary().revoked());
  }

  @Test
  void shallCreateTerminationWithPassword() {
    assertEquals(PASSWORD,
        terminationBuilder().create().export().password().password());
  }

  @Test
  void shallCreateTerminationWithExportTime() {
    final var expectedTime = LocalDateTime.now();
    assertEquals(expectedTime,
        terminationBuilder().exportTime(expectedTime).create().export().exportTime());
  }

  @Test
  void shallCreateTerminationWithNotificationTime() {
    final var expectedTime = LocalDateTime.now();
    assertEquals(expectedTime,
        terminationBuilder().notificationTime(expectedTime).create().export().notificationTime());
  }

  @Test
  void shallCreateTerminationWithReminderTime() {
    final var expectedTime = LocalDateTime.now();
    assertEquals(expectedTime,
        terminationBuilder().reminderTime(expectedTime).create().export().reminderTime());
  }

  @Test
  void shallCreateTerminationWithReceiptTime() {
    final var expectedTime = LocalDateTime.now();
    assertEquals(expectedTime,
        terminationBuilder().receiptTime(expectedTime).create().export().receiptTime());
  }

  @Test
  void shallNotAcceptTerminationWithoutCreatorHSAId() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().creatorHSAId("").create());
    assertEquals("Missing HSAId", exception.getMessage());
  }

  @Test
  void shallNotAcceptTerminationWithoutCreatorName() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().creatorName("").create());
    assertEquals("Missing Name", exception.getMessage());
  }

  @Test
  void shallNotAcceptTerminationWithoutHSAId() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().careProviderHSAId("").create());
    assertEquals("Missing HSAId", exception.getMessage());
  }

  @Test
  void shallNotAcceptTerminationWithoutOrganizationNumber() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().careProviderOrganizationNumber("").create());
    assertEquals("Missing OrganizationNumber", exception.getMessage());
  }

  @Test
  void shallNotAcceptTerminationWithoutPersonId() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().careProviderOrganizationRepresentativePersonId("")
            .create());
    assertEquals("Missing PersonId", exception.getMessage());
  }

  @Test
  void shallNotAcceptTerminationWithoutPhoneNumber() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().careProviderOrganizationRepresentativePhoneNumber("")
            .create());
    assertEquals("Missing PhoneNumber", exception.getMessage());
  }

  @Test
  void shallNotAcceptTerminationWithoutEmailAddress() {
    final var exception = assertThrows(IllegalArgumentException.class,
        () -> terminationBuilder().careProviderOrganizationRepresentativeEmailAddress("")
            .create());
    assertEquals("Missing EmailAddress", exception.getMessage());
  }

  private TerminationBuilder terminationBuilder() {
    return TerminationBuilder.getInstance()
        .creatorHSAId(CREATOR_HSA_ID)
        .creatorName(CREATOR_NAME)
        .careProviderHSAId(HSA_ID)
        .careProviderOrganizationNumber(ORGANIZATION_NUMBER)
        .careProviderOrganizationRepresentativePersonId(PERSON_ID)
        .careProviderOrganizationRepresentativePhoneNumber(PHONE_NUMBER)
        .careProviderOrganizationRepresentativeEmailAddress(EMAIL_ADDRESS)
        .total(TOTAL)
        .revoked(REVOKED)
        .packagePassword(PASSWORD);
  }
}