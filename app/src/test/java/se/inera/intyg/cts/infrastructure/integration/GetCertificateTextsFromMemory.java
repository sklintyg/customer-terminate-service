package se.inera.intyg.cts.infrastructure.integration;

import java.util.Collections;
import java.util.List;
import se.inera.intyg.cts.domain.model.CertificateText;

public class GetCertificateTextsFromMemory implements GetCertificateTexts {

  private List<CertificateText> certificateTexts;

  @Override
  public List<CertificateText> get() {
    return Collections.unmodifiableList(certificateTexts);
  }

  public void prepare(List<CertificateText> certificateTexts) {
    this.certificateTexts = certificateTexts;
  }
}
