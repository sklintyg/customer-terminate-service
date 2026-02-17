package se.inera.intyg.cts.domain.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Termination {

  private final static List<TerminationStatus> ALLOWED_TO_UPDATE = Arrays.asList(
      TerminationStatus.CREATED,
      TerminationStatus.COLLECTING_CERTIFICATES,
      TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED,
      TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
      TerminationStatus.EXPORTED,
      TerminationStatus.NOTIFICATION_SENT,
      TerminationStatus.REMINDER_SENT
  );

  private final static List<TerminationStatus> NEED_REEXPORT_STATUS = List.of(
      TerminationStatus.EXPORTED,
      TerminationStatus.NOTIFICATION_SENT,
      TerminationStatus.REMINDER_SENT
  );

  private final static List<TerminationStatus> NEED_RENOTIFICATION_STATUS = List.of(
      TerminationStatus.NOTIFICATION_SENT,
      TerminationStatus.REMINDER_SENT);

  private final TerminationId terminationId;
  private final LocalDateTime created;
  private LocalDateTime modified;
  private final Staff creator;
  private CareProvider careProvider;
  private TerminationStatus status;
  private final Export export;
  private Erase erase;

  Termination(TerminationId terminationId, LocalDateTime created, LocalDateTime modified,
      Staff creator, CareProvider careProvider, TerminationStatus status, Export export,
      Erase erase) {
    this.modified = modified;
    if (terminationId == null) {
      throw new IllegalArgumentException("Missing TerminationId");
    }
    if (created == null) {
      throw new IllegalArgumentException("Missing Created");
    }
    if (modified == null) {
      throw new IllegalArgumentException("Missing Modified");
    }
    if (creator == null) {
      throw new IllegalArgumentException("Missing Creator");
    }
    if (careProvider == null) {
      throw new IllegalArgumentException("Missing CareProvider");
    }
    if (status == null) {
      throw new IllegalArgumentException("Missing Status");
    }
    if (export == null) {
      throw new IllegalArgumentException("Missing Export");
    }
    if (erase == null) {
      throw new IllegalArgumentException("Missing Erase");
    }
    this.terminationId = terminationId;
    this.created = created;
    this.modified = modified;
    this.creator = creator;
    this.careProvider = careProvider;
    this.status = status;
    this.export = export;
    this.erase = erase;
  }

  public void collect(CertificateBatch certificateBatch) {
    if (status == TerminationStatus.CREATED) {
      status = TerminationStatus.COLLECTING_CERTIFICATES;
    }

    export().processBatch(certificateBatch);

    if (export().certificateSummary().equals(certificateBatch.certificateSummary())) {
      status = TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED;
    }
  }

  public void collect(List<CertificateText> certificateTexts) {
    status = TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED;
  }

  public void exported(Password password) {
    export().packagePassword(password);
    export().exportTime(LocalDateTime.now());
    status = TerminationStatus.EXPORTED;
  }

  public void notificationSent() {
    export().notificationTime(LocalDateTime.now());
    status = TerminationStatus.NOTIFICATION_SENT;
  }

  public void reminderSent() {
    export().reminderTime(LocalDateTime.now());
    status = TerminationStatus.REMINDER_SENT;
  }

  public void receiptReceived(LocalDateTime receiptTime) {
    export().receiptTime(receiptTime);
    status = TerminationStatus.RECEIPT_RECEIVED;
  }

  public void passwordSent() {
    status = TerminationStatus.PASSWORD_SENT;
  }

  public void passwordResent() {
    status = TerminationStatus.PASSWORD_RESENT;
  }

  public TerminationId terminationId() {
    return terminationId;
  }

  public LocalDateTime created() {
    return created;
  }

  public LocalDateTime modified() {
    return modified;
  }

  public Staff creator() {
    return creator;
  }

  public CareProvider careProvider() {
    return careProvider;
  }

  public TerminationStatus status() {
    return status;
  }

  public Export export() {
    return export;
  }

  public Erase erase() {
    return erase;
  }

  public void initiateErase() {
    status = TerminationStatus.START_ERASE;
  }

  public void startErase(List<EraseService> eraseServices) {
    erase = new Erase(eraseServices);
    status = TerminationStatus.ERASE_IN_PROGRESS;
  }

  public void eraseCancelled() {
    status = TerminationStatus.ERASE_CANCELLED;
  }

  public void erased(ServiceId serviceId) {
    erase = new Erase(
        erase.eraseServices().stream()
            .map(service -> {
              if (service.serviceId().equals(serviceId)) {
                return new EraseService(serviceId, true);
              }
              return service;
            })
            .collect(Collectors.toList())
    );

    if (erase.eraseServices().stream().allMatch(eraseService -> eraseService.erased())) {
      status = TerminationStatus.ERASE_COMPLETED;
    }
  }

  @Override
  public String toString() {
    return "Termination{" +
        "terminationId=" + terminationId +
        ", created=" + created +
        ", modified=" + modified +
        ", creator=" + creator +
        ", careProvider=" + careProvider +
        ", status=" + status +
        ", export=" + export +
        ", erase=" + erase +
        '}';
  }

  public void update(HSAId hsaId, PersonId personId, EmailAddress emailAddress,
      PhoneNumber phoneNumber) {
    if (notAllowedToUpdate()) {
      throw new IllegalStateException(
          String.format(
              "Not allowed to update because termination '%s' has status '%s'!",
              terminationId.id(), status)
      );
    }

    if (!careProvider.hsaId().equals(hsaId)) {
      updateHsaId(hsaId);
    }

    if (!export.organizationRepresentative().personId().equals(personId)) {
      updatePersonId(personId);
    }

    if (!export.organizationRepresentative().emailAddress().equals(emailAddress)) {
      updateEmailAdress(emailAddress);
    }

    if (!export.organizationRepresentative().phoneNumber().equals(phoneNumber)) {
      updatePhoneNumber(phoneNumber);
    }
  }

  private void updateHsaId(HSAId hsaId) {
    careProvider = new CareProvider(hsaId, careProvider.organizationNumber());
    status = newStatusWhenHsaIdIsUpdated();
    export.reset();
    modified = LocalDateTime.now();
  }

  private void updatePersonId(PersonId personId) {
    export.update(new OrganizationRepresentative(
        personId,
        export.organizationRepresentative().phoneNumber(),
        export.organizationRepresentative().emailAddress()
    ));

    if (isReExportNeeded()) {
      status = newStatusForReExport();
    }

    modified = LocalDateTime.now();
    resetExportTimestamps();
  }

  private void updatePhoneNumber(PhoneNumber phoneNumber) {
    export.update(new OrganizationRepresentative(
        export.organizationRepresentative().personId(),
        phoneNumber,
        export.organizationRepresentative().emailAddress()
    ));

    if (isReNotificationNeeded()) {
      status = newStatusForReNotification();
    }

    modified = LocalDateTime.now();
    resetNotificationTimestamps();
  }

  private void updateEmailAdress(EmailAddress emailAddress) {
    export.update(new OrganizationRepresentative(
        export.organizationRepresentative().personId(),
        export.organizationRepresentative().phoneNumber(),
        emailAddress
    ));

    if (isReNotificationNeeded()) {
      status = newStatusForReNotification();
    }

    modified = LocalDateTime.now();
    resetNotificationTimestamps();
  }

  private boolean notAllowedToUpdate() {
    return !ALLOWED_TO_UPDATE.contains(status);
  }

  private TerminationStatus newStatusWhenHsaIdIsUpdated() {
    return TerminationStatus.CREATED;
  }

  private boolean isReExportNeeded() {
    return NEED_REEXPORT_STATUS.contains(status);
  }

  private TerminationStatus newStatusForReExport() {
    return TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED;
  }

  private boolean isReNotificationNeeded() {
    return NEED_RENOTIFICATION_STATUS.contains(status);
  }

  private TerminationStatus newStatusForReNotification() {
    return TerminationStatus.EXPORTED;
  }

  private void resetExportTimestamps() {
    export.exportTime(null);
    resetNotificationTimestamps();
  }

  private void resetNotificationTimestamps() {
    export.notificationTime(null);
    export.reminderTime(null);
  }
}
