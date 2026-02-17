package se.inera.intyg.cts.infrastructure.integration;

import java.util.List;
import se.inera.intyg.cts.domain.model.CertificateText;

public interface GetCertificateTexts {

  List<CertificateText> get();
}
