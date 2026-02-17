package se.inera.intyg.cts.application.service;

import org.springframework.transaction.annotation.Transactional;

public interface ExportService {

    void collectCertificatesToExport();

    void collectCertificateTextsToExport();

    void export();
}
