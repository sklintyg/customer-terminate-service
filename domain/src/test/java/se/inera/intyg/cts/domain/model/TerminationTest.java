package se.inera.intyg.cts.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.defaultTerminationBuilder;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TerminationTest {

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class UpdateHsaId {

    Stream<TerminationStatus> statusAllowedToUpdateHsaId() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallUpdateOfHsaIdIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(newHsaId, termination.careProvider().hsaId());
    }

    Stream<TerminationStatus> statusBlockedToUpdateHsaId() {
      return Stream.of(
          TerminationStatus.RECEIPT_RECEIVED,
          TerminationStatus.PASSWORD_SENT,
          TerminationStatus.START_ERASE,
          TerminationStatus.ERASE_IN_PROGRESS,
          TerminationStatus.ERASE_CANCELLED,
          TerminationStatus.ERASE_COMPLETED
      );
    }

    @ParameterizedTest
    @MethodSource("statusBlockedToUpdateHsaId")
    void shallBlockUpdateOfHsaIdIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var exception = assertThrows(IllegalStateException.class,
          () ->
              termination.update(
                  newHsaId,
                  termination.export().organizationRepresentative().personId(),
                  termination.export().organizationRepresentative().emailAddress(),
                  termination.export().organizationRepresentative().phoneNumber()
              ));

      assertTrue(exception.getMessage().contains("Not allowed to update"),
          () -> exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallNotUpdateHsaIdIfItHasntChanged(TerminationStatus terminationStatus) {
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var expectedHsaId = termination.careProvider().hsaId();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(expectedHsaId, termination.careProvider().hsaId());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallResetStatusToCreatedWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(TerminationStatus.CREATED, termination.status());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallResetCertificateSummaryWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .total(100)
          .revoked(10)
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(new CertificateSummary(0, 0), termination.export().certificateSummary());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallResetPasswordWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .total(100)
          .revoked(10)
          .packagePassword("Password")
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().password(),
          () -> "Password should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallResetExportTimeWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .total(100)
          .revoked(10)
          .exportTime(LocalDateTime.now())
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().exportTime(),
          () -> "Export time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallResetNotificationTimeWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .total(100)
          .revoked(10)
          .notificationTime(LocalDateTime.now())
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().notificationTime(),
          () -> "Notification time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallResetReminderTimeWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .total(100)
          .revoked(10)
          .reminderTime(LocalDateTime.now())
          .create();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().reminderTime(),
          () -> "Reminder time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateHsaId")
    void shallUpdateModifiedWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .total(100)
          .revoked(10)
          .packagePassword("Password")
          .create();

      final var beforeUpdate = termination.modified();

      termination.update(
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertTrue(termination.modified().isAfter(beforeUpdate), () ->
          String.format("Expect modified '%s' to be updated and after '%s'",
              termination.modified(), beforeUpdate)
      );
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class UpdatePersonId {

    Stream<TerminationStatus> statusAllowedToUpdatePersonId() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePersonId")
    void shallUpdateOfHsaIdIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(newPersonId, termination.export().organizationRepresentative().personId());
    }

    Stream<TerminationStatus> statusBlockedToUpdateHsaId() {
      return Stream.of(
          TerminationStatus.RECEIPT_RECEIVED,
          TerminationStatus.PASSWORD_SENT,
          TerminationStatus.START_ERASE,
          TerminationStatus.ERASE_IN_PROGRESS,
          TerminationStatus.ERASE_CANCELLED,
          TerminationStatus.ERASE_COMPLETED
      );
    }

    @ParameterizedTest
    @MethodSource("statusBlockedToUpdateHsaId")
    void shallBlockUpdateOfHsaIdIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var exception = assertThrows(IllegalStateException.class,
          () ->
              termination.update(
                  termination.careProvider().hsaId(),
                  newPersonId,
                  termination.export().organizationRepresentative().emailAddress(),
                  termination.export().organizationRepresentative().phoneNumber()
              )
      );

      assertTrue(exception.getMessage().contains("Not allowed to update"),
          () -> exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePersonId")
    void shallNotUpdateHsaIdIfItHasntChanged(TerminationStatus terminationStatus) {
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var expectedPersonId = termination.export().organizationRepresentative().personId();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(expectedPersonId,
          termination.export().organizationRepresentative().personId());
    }

    Stream<TerminationStatus> statusToResetExport() {
      return Stream.of(
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusToResetExport")
    void shallResetStatusToPreExportWhenPersonIdIsUpdated(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          termination.status());
    }

    Stream<TerminationStatus> statusToLeaveUnchanged() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED
      );
    }

    @ParameterizedTest
    @MethodSource("statusToLeaveUnchanged")
    void shallLeaveStatusUnchangedWhenPersonIdIsUpdated(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(terminationStatus, termination.status());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePersonId")
    void shallUpdateModifiedWhenPersonIdIsUpdated(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var beforeUpdate = termination.modified();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertTrue(termination.modified().isAfter(beforeUpdate), () ->
          String.format("Expect modified '%s' to be updated and after '%s'",
              termination.modified(), beforeUpdate)
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePersonId")
    void shallResetExportTimeWhenPersonIdIsUpdated(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .exportTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().exportTime(),
          () -> "Notification time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePersonId")
    void shallResetNotificationTimeWhenPersonIdIsUpdated(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .notificationTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().notificationTime(),
          () -> "Notification time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePersonId")
    void shallResetReminderTimeWhenPersonIdIsUpdated(TerminationStatus terminationStatus) {
      final var newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .reminderTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().reminderTime(),
          () -> "Reminder time should be reset to null!");
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class UpdateEmail {

    Stream<TerminationStatus> statusAllowedToUpdateEmail() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateEmail")
    void shallUpdateOfEmailIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAdress,
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(newEmailAdress,
          termination.export().organizationRepresentative().emailAddress());
    }

    Stream<TerminationStatus> statusBlockedToUpdateEmail() {
      return Stream.of(
          TerminationStatus.RECEIPT_RECEIVED,
          TerminationStatus.PASSWORD_SENT,
          TerminationStatus.START_ERASE,
          TerminationStatus.ERASE_IN_PROGRESS,
          TerminationStatus.ERASE_CANCELLED,
          TerminationStatus.ERASE_COMPLETED
      );
    }

    @ParameterizedTest
    @MethodSource("statusBlockedToUpdateEmail")
    void shallBlockUpdateOfEmailIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var exception = assertThrows(IllegalStateException.class,
          () ->
              termination.update(
                  termination.careProvider().hsaId(),
                  termination.export().organizationRepresentative().personId(),
                  newEmailAdress,
                  termination.export().organizationRepresentative().phoneNumber()
              )
      );

      assertTrue(exception.getMessage().contains("Not allowed to update"),
          () -> exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateEmail")
    void shallNotUpdateEmailIfItHasntChanged(TerminationStatus terminationStatus) {
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var expectedEmail = termination.export().organizationRepresentative().emailAddress();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(expectedEmail,
          termination.export().organizationRepresentative().emailAddress());
    }

    Stream<TerminationStatus> statusToResetNotifications() {
      return Stream.of(
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusToResetNotifications")
    void shallResetStatusToPreExportWhenEmailIsUpdated(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAdress,
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(TerminationStatus.EXPORTED,
          termination.status());
    }

    Stream<TerminationStatus> statusToLeaveUnchanged() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED
      );
    }

    @ParameterizedTest
    @MethodSource("statusToLeaveUnchanged")
    void shallLeaveStatusUnchangedWhenEmailIsUpdated(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAdress,
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(terminationStatus, termination.status());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateEmail")
    void shallUpdateModifiedWhenEmailIsUpdated(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var beforeUpdate = termination.modified();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAdress,
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertTrue(termination.modified().isAfter(beforeUpdate), () ->
          String.format("Expect modified '%s' to be updated and after '%s'",
              termination.modified(), beforeUpdate)
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateEmail")
    void shallResetNotificationTimeWhenEmailIsUpdated(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .notificationTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAdress,
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().notificationTime(),
          () -> "Notification time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdateEmail")
    void shallResetReminderTimeWhenEmailIsUpdated(TerminationStatus terminationStatus) {
      final var newEmailAdress = new EmailAddress("NewEmailAdress");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .reminderTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAdress,
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertNull(termination.export().reminderTime(),
          () -> "Reminder time should be reset to null!");
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class UpdatePhoneNumber {

    Stream<TerminationStatus> statusAllowedToUpdatePhoneNumber() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePhoneNumber")
    void shallUpdateOfPhoneNumberIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );

      assertEquals(newPhoneNumber,
          termination.export().organizationRepresentative().phoneNumber());
    }

    Stream<TerminationStatus> statusBlockedToUpdatePhoneNumber() {
      return Stream.of(
          TerminationStatus.RECEIPT_RECEIVED,
          TerminationStatus.PASSWORD_SENT,
          TerminationStatus.START_ERASE,
          TerminationStatus.ERASE_IN_PROGRESS,
          TerminationStatus.ERASE_CANCELLED,
          TerminationStatus.ERASE_COMPLETED
      );
    }

    @ParameterizedTest
    @MethodSource("statusBlockedToUpdatePhoneNumber")
    void shallBlockUpdateOfOPhoneNumberIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var exception = assertThrows(IllegalStateException.class,
          () ->
              termination.update(
                  termination.careProvider().hsaId(),
                  termination.export().organizationRepresentative().personId(),
                  termination.export().organizationRepresentative().emailAddress(),
                  newPhoneNumber
              )
      );

      assertTrue(exception.getMessage().contains("Not allowed to update"),
          () -> exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePhoneNumber")
    void shallNotUpdatePhoneNumberIfItHasntChanged(TerminationStatus terminationStatus) {
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var expectedPhoneNumber = termination.export().organizationRepresentative()
          .phoneNumber();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      assertEquals(expectedPhoneNumber,
          termination.export().organizationRepresentative().phoneNumber());
    }

    Stream<TerminationStatus> statusToResetNotifications() {
      return Stream.of(
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusToResetNotifications")
    void shallResetStatusToPreExportWhenPhoneNumberIsUpdated(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );

      assertEquals(TerminationStatus.EXPORTED, termination.status());
    }

    Stream<TerminationStatus> statusToLeaveUnchanged() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED
      );
    }

    @ParameterizedTest
    @MethodSource("statusToLeaveUnchanged")
    void shallLeaveStatusUnchangedWhenPhoneNumberIsUpdated(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );

      assertEquals(terminationStatus, termination.status());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePhoneNumber")
    void shallUpdateModifiedWhenPhoneNumberIsUpdated(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      final var beforeUpdate = termination.modified();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );

      assertTrue(termination.modified().isAfter(beforeUpdate), () ->
          String.format("Expect modified '%s' to be updated and after '%s'",
              termination.modified(), beforeUpdate)
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePhoneNumber")
    void shallResetNotificationTimeWhenPhoneNumberIsUpdated(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .notificationTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );

      assertNull(termination.export().notificationTime(),
          () -> "Notification time should be reset to null!");
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdatePhoneNumber")
    void shallResetReminderTimeWhenPhoneNumberIsUpdated(TerminationStatus terminationStatus) {
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .reminderTime(LocalDateTime.now())
          .create();

      termination.update(
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );

      assertNull(termination.export().reminderTime(),
          () -> "Reminder time should be reset to null!");
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class UpdateAll {

    Stream<TerminationStatus> statusAllowedToUpdate() {
      return Stream.of(
          TerminationStatus.CREATED,
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdate")
    void shallUpdateOfHsaIdIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var newPersonId = new PersonId("NewPersonId");
      final var newEmailAddress = new EmailAddress("NewEmailAddress");
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          newPersonId,
          newEmailAddress,
          newPhoneNumber
      );

      assertEquals(newHsaId, termination.careProvider().hsaId());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdate")
    void shallUpdateOfPersonIdIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var newPersonId = new PersonId("NewPersonId");
      final var newEmailAddress = new EmailAddress("NewEmailAddress");
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          newPersonId,
          newEmailAddress,
          newPhoneNumber
      );

      assertEquals(newPersonId,
          termination.export().organizationRepresentative().personId());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdate")
    void shallUpdateOfEmailAddressIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var newPersonId = new PersonId("NewPersonId");
      final var newEmailAddress = new EmailAddress("NewEmailAddress");
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          newPersonId,
          newEmailAddress,
          newPhoneNumber
      );

      assertEquals(newEmailAddress,
          termination.export().organizationRepresentative().emailAddress());
    }

    @ParameterizedTest
    @MethodSource("statusAllowedToUpdate")
    void shallUpdateOfPhoneNumberIfOfFollowingStatus(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var newPersonId = new PersonId("NewPersonId");
      final var newEmailAddress = new EmailAddress("NewEmailAddress");
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          newPersonId,
          newEmailAddress,
          newPhoneNumber
      );

      assertEquals(newPhoneNumber,
          termination.export().organizationRepresentative().phoneNumber());
    }

    Stream<TerminationStatus> statusToResetTermination() {
      return Stream.of(
          TerminationStatus.COLLECTING_CERTIFICATES,
          TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
          TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          TerminationStatus.EXPORTED,
          TerminationStatus.NOTIFICATION_SENT,
          TerminationStatus.REMINDER_SENT
      );
    }

    @ParameterizedTest
    @MethodSource("statusToResetTermination")
    void shallResetStatusToCreatedWhenHsaIdIsUpdated(TerminationStatus terminationStatus) {
      final var newHsaId = new HSAId("NewHsaId");
      final var newPersonId = new PersonId("NewPersonId");
      final var newEmailAddress = new EmailAddress("NewEmailAddress");
      final var newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(terminationStatus)
          .create();

      termination.update(
          newHsaId,
          newPersonId,
          newEmailAddress,
          newPhoneNumber
      );

      assertEquals(TerminationStatus.CREATED, termination.status());
    }
  }
}