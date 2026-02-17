package se.inera.intyg.cts.infrastructure.integration.sjut.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PackageMetadataTest {

  private static final String ORGANIZATION_NAME = "Organization name";
  private static final String ORGANIZATION_NUMBER = "Organization number";
  private static final String DELEGATE_PNR = "191212121212";
  private static final String SOURCE_SYSTEM = "Source system";
  private static final String RECEIPT_URL = "https://test/api/v1/receipt";

  @Test
  void shallRemoveDashFromDelegateNumber() {
    final var packageMetadata = new PackageMetadata(ORGANIZATION_NAME, ORGANIZATION_NUMBER,
        "19121212-1212", SOURCE_SYSTEM, RECEIPT_URL);
    assertEquals(DELEGATE_PNR, packageMetadata.delegatePnr());
  }
}