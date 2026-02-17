package se.inera.intyg.cts.domain.model;

import java.time.LocalDateTime;

public class Export {

  private OrganizationRepresentative organizationRepresentative;
  private CertificateSummary certificateSummary;
  private Password password;
  private LocalDateTime exportTime;
  private LocalDateTime notificationTime;
  private LocalDateTime reminderTime;
  private LocalDateTime receiptTime;

  Export(OrganizationRepresentative organizationRepresentative) {
    this(organizationRepresentative, new CertificateSummary(0, 0), null, null, null, null, null);
  }

  Export(OrganizationRepresentative organizationRepresentative,
      CertificateSummary certificateSummary, Password password, LocalDateTime exportTime,
      LocalDateTime notificationTime, LocalDateTime reminderTime, LocalDateTime receiptTime) {
    if (organizationRepresentative == null) {
      throw new IllegalArgumentException("Missing OrganisationalRepresentative");
    }
    if (certificateSummary == null) {
      throw new IllegalArgumentException("Missing CertificateSummary");
    }
    this.organizationRepresentative = organizationRepresentative;
    this.certificateSummary = certificateSummary;
    this.password = password;
    this.exportTime = exportTime;
    this.notificationTime = notificationTime;
    this.reminderTime = reminderTime;
    this.receiptTime = receiptTime;
  }

  public void processBatch(CertificateBatch certificateBatch) {
    final var total = certificateBatch.certificateList().size();
    final var revokedCount = (int) certificateBatch.certificateList().stream()
        .filter(Certificate::revoked)
        .count();

    certificateSummary = certificateSummary.add(new CertificateSummary(total, revokedCount));
  }

  public void packagePassword(Password password) {
    this.password = password;
  }

  public OrganizationRepresentative organizationRepresentative() {
    return organizationRepresentative;
  }

  public CertificateSummary certificateSummary() {
    return certificateSummary;
  }

  public Password password() {
    return password;
  }

  public LocalDateTime exportTime() {
    return exportTime;
  }

  public void exportTime(LocalDateTime exportTime) {
    this.exportTime = exportTime;
  }

  public LocalDateTime notificationTime() {
    return notificationTime;
  }

  public void notificationTime(LocalDateTime notificationTime) {
    this.notificationTime = notificationTime;
  }

  public LocalDateTime reminderTime() {
    return reminderTime;
  }

  public void reminderTime(LocalDateTime reminderTime) {
    this.reminderTime = reminderTime;
  }

  public LocalDateTime receiptTime() {
    return receiptTime;
  }

  public void receiptTime(LocalDateTime receiptTime) {
    this.receiptTime = receiptTime;
  }

  public void update(OrganizationRepresentative organizationRepresentative) {
    this.organizationRepresentative = organizationRepresentative;
  }

  public void reset() {
    this.password = null;
    this.certificateSummary = new CertificateSummary(0, 0);
    this.exportTime = null;
    this.notificationTime = null;
    this.reminderTime = null;
  }

  @Override
  public String toString() {
    return "Export{" +
        "organizationRepresentative=" + organizationRepresentative +
        ", certificateSummary=" + certificateSummary +
        ", password=" + password +
        ", receiptTime=" + receiptTime +
        '}';
  }
}
