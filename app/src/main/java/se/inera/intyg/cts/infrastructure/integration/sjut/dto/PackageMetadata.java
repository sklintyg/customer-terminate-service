package se.inera.intyg.cts.infrastructure.integration.sjut.dto;

public record PackageMetadata(String organizationName,
                              String organizationNumber,
                              String delegatePnr,
                              String sourceSystem,
                              String receiptUrl) {

  public PackageMetadata {
    if (organizationName == null || organizationName.isBlank()) {
      throw new IllegalArgumentException("OrganizationName cannot be null or blank");
    }
    if (organizationNumber == null || organizationNumber.isBlank()) {
      throw new IllegalArgumentException("OrganizationNumber cannot be null or blank");
    }
    if (delegatePnr == null || delegatePnr.isBlank()) {
      throw new IllegalArgumentException("DelegatePnr cannot be null or blank");
    }
    if (sourceSystem == null || sourceSystem.isBlank()) {
      throw new IllegalArgumentException("SourceSystem cannot be null or blank");
    }
    if (receiptUrl == null || receiptUrl.isBlank()) {
      throw new IllegalArgumentException("ReceiptUrl cannot be null or blank");
    }

    delegatePnr = delegatePnr.replace("-", "");
  }
}
