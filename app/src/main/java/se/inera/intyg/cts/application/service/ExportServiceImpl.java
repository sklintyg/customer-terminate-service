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
package se.inera.intyg.cts.application.service;

import java.util.Arrays;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.CollectExportContent;
import se.inera.intyg.cts.domain.service.ExportPackage;

@Service
public class ExportServiceImpl implements ExportService {

  private static final Logger LOG = LoggerFactory.getLogger(ExportServiceImpl.class);

  private final TerminationRepository terminationRepository;
  private final CollectExportContent collectExportContent;
  private final ExportPackage exportPackage;

  public ExportServiceImpl(
      TerminationRepository terminationRepository,
      CollectExportContent collectExportContent,
      ExportPackage exportPackage) {
    this.terminationRepository = terminationRepository;
    this.collectExportContent = collectExportContent;
    this.exportPackage = exportPackage;
  }

  @Override
  @Transactional
  public void collectCertificatesToExport() {
    final var terminations =
        terminationRepository.findByStatuses(
            Arrays.asList(TerminationStatus.CREATED, TerminationStatus.COLLECTING_CERTIFICATES));

    terminations.forEach(
        termination -> {
          try {
            collectExportContent.collectCertificates(termination.terminationId());
            LOG.info(
                "Collected certificates for termination '{}'", termination.terminationId().id());
          } catch (Exception ex) {
            LOG.error(
                String.format(
                    "Failed to collect certificates for termination '%s'",
                    termination.terminationId().id()),
                ex);
          }
        });
  }

  @Override
  @Transactional
  public void collectCertificateTextsToExport() {
    final var terminationsToExport =
        terminationRepository.findByStatuses(
            Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED));

    terminationsToExport.forEach(
        termination -> {
          try {
            collectExportContent.collectCertificateTexts(termination);
            LOG.info(
                "Collected certificate texts for termination '{}'",
                termination.terminationId().id());
          } catch (Exception ex) {
            LOG.error(
                String.format(
                    "Failed to collect certificate texts for termination '%s'",
                    termination.terminationId().id()),
                ex);
          }
        });
  }

  @Override
  @Transactional
  public void export() {
    final var terminationsToExport =
        terminationRepository.findByStatuses(
            Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED));

    terminationsToExport.forEach(
        termination -> {
          try {
            exportPackage.export(termination);
            LOG.info("Exported termination '{}'", termination.terminationId().id());
          } catch (Exception ex) {
            LOG.error(
                String.format(
                    "Failed to export termination '%s'", termination.terminationId().id()),
                ex);
          }
        });
  }
}
