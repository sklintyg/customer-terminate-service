package se.inera.intyg.cts.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TerminationBuilder {

  private UUID terminationId;
  private LocalDateTime created;
  private LocalDateTime modified;
  private String creatorHSAId;
  private String creatorName;
  private String careProviderHSAId;
  private String careProviderOrganizationNumber;
  private String careProviderOrganizationRepresentativePersonId;
  private String careProviderOrganizationRepresentativePhoneNumber;
  private String careProviderOrganizationRepresentativeEmailAddress;
  private TerminationStatus status;
  private int total;
  private int revoked;
  private String packagePassword;
  private LocalDateTime exportTime;
  private LocalDateTime notificationTime;
  private LocalDateTime reminderTime;
  private LocalDateTime receiptTime;
  private List<EraseService> eraseServices;

  public static TerminationBuilder getInstance() {
    return new TerminationBuilder();
  }

  public TerminationBuilder terminationId(UUID terminationId) {
    this.terminationId = terminationId;
    return this;
  }

  public TerminationBuilder created(LocalDateTime created) {
    this.created = created;
    return this;
  }

  public TerminationBuilder modified(LocalDateTime modified) {
    this.modified = modified;
    return this;
  }

  public TerminationBuilder creatorHSAId(String creatorHSAId) {
    this.creatorHSAId = creatorHSAId;
    return this;
  }

  public TerminationBuilder creatorName(String creatorName) {
    this.creatorName = creatorName;
    return this;
  }

  public TerminationBuilder careProviderHSAId(String hsaId) {
    this.careProviderHSAId = hsaId;
    return this;
  }

  public TerminationBuilder careProviderOrganizationNumber(String organizationNumber) {
    this.careProviderOrganizationNumber = organizationNumber;
    return this;
  }

  public TerminationBuilder careProviderOrganizationRepresentativePersonId(String personId) {
    this.careProviderOrganizationRepresentativePersonId = personId;
    return this;
  }

  public TerminationBuilder careProviderOrganizationRepresentativePhoneNumber(
      String phoneNumber) {
    this.careProviderOrganizationRepresentativePhoneNumber = phoneNumber;
    return this;
  }

  public TerminationBuilder careProviderOrganizationRepresentativeEmailAddress(
      String emailAddress) {
    this.careProviderOrganizationRepresentativeEmailAddress = emailAddress;
    return this;
  }

  public TerminationBuilder status(TerminationStatus status) {
    this.status = status;
    return this;
  }

  public TerminationBuilder total(int total) {
    this.total = total;
    return this;
  }

  public TerminationBuilder revoked(int revoked) {
    this.revoked = revoked;
    return this;
  }

  public TerminationBuilder packagePassword(String packagePassword) {
    this.packagePassword = packagePassword;
    return this;
  }

  public TerminationBuilder exportTime(LocalDateTime exportTime) {
    this.exportTime = exportTime;
    return this;
  }

  public TerminationBuilder notificationTime(LocalDateTime notificationTime) {
    this.notificationTime = notificationTime;
    return this;
  }

  public TerminationBuilder reminderTime(LocalDateTime reminderTime) {
    this.reminderTime = reminderTime;
    return this;
  }

  public TerminationBuilder receiptTime(LocalDateTime receiptTime) {
    this.receiptTime = receiptTime;
    return this;
  }

  public TerminationBuilder eraseServices(List<EraseService> eraseServices) {
    this.eraseServices = eraseServices;
    return this;
  }

  public Termination create() {
    if (terminationId == null) {
      terminationId = UUID.randomUUID();
      created = LocalDateTime.now();
      modified = created;
      status = TerminationStatus.CREATED;
    }

    final var creator = new Staff(new HSAId(creatorHSAId), creatorName);
    final var hsaId = new HSAId(careProviderHSAId);
    final var organizationNumber = new OrganizationNumber(careProviderOrganizationNumber);
    final var personId = new PersonId(careProviderOrganizationRepresentativePersonId);
    final var phoneNumber = new PhoneNumber(careProviderOrganizationRepresentativePhoneNumber);
    final var emailAddress = new EmailAddress(careProviderOrganizationRepresentativeEmailAddress);

    return new Termination(
        new TerminationId(terminationId),
        created,
        modified,
        creator,
        new CareProvider(hsaId, organizationNumber),
        status,
        new Export(
            new OrganizationRepresentative(personId, phoneNumber, emailAddress),
            new CertificateSummary(total, revoked),
            packagePassword != null ? new Password(packagePassword) : null,
            exportTime,
            notificationTime,
            reminderTime,
            receiptTime
        ),
        new Erase(eraseServices == null ? new ArrayList<>() : eraseServices)
    );
  }
}