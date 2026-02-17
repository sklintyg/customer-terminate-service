package se.inera.intyg.cts.infrastructure.integration;

import se.inera.intyg.cts.domain.model.CertificateBatch;

public interface GetCertificateBatch {

  CertificateBatch get(String careProvider, int limit, int offset);
}
