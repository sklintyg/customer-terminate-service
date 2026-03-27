/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.infrastructure.integration;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.domain.model.CertificateBatch;
import se.inera.intyg.cts.domain.model.CertificateSummary;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;

@Repository
public class IntegrationCertificateBatchRepository implements CertificateBatchRepository {

  private final GetCertificateBatch getCertificateBatch;
  private final int batchSize;
  private final GetCertificateTexts getCertificateTexts;

  public IntegrationCertificateBatchRepository(
      GetCertificateBatch getCertificateBatch,
      @Value("${certificate.batch.size:30}") int batchSize,
      GetCertificateTexts getCertificateTexts) {
    this.getCertificateBatch = getCertificateBatch;
    this.batchSize = batchSize;
    this.getCertificateTexts = getCertificateTexts;
  }

  @Override
  public CertificateSummary certificateSummary(Termination termination) {
    return getCertificateBatch
        .get(termination.careProvider().hsaId().id(), 1, 0)
        .certificateSummary();
  }

  @Override
  public CertificateBatch nextBatch(Termination termination) {
    return getCertificateBatch.get(
        termination.careProvider().hsaId().id(),
        batchSize,
        termination.export().certificateSummary().total());
  }

  @Override
  public List<CertificateText> certificateTexts(Termination termination) {
    return getCertificateTexts.get();
  }
}
