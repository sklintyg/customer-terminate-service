package se.inera.intyg.cts.domain.model;

import java.util.Collections;
import java.util.List;

public record CertificateBatch(CertificateSummary certificateSummary,
                               List<Certificate> certificateList) {

  public CertificateBatch {
    if (certificateSummary == null) {
      throw new IllegalArgumentException("CertificateSummary cannot be null");
    }
    if (certificateList == null) {
      throw new IllegalArgumentException("CertificateList cannot be null");
    }
  }

  public static CertificateBatch emptyBatch() {
    return new CertificateBatch(new CertificateSummary(0, 0), Collections.emptyList());
  }
}
