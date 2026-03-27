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
package se.inera.intyg.cts.application.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.application.service.ExportService;
import se.inera.intyg.cts.application.service.MessageService;

@ExtendWith(MockitoExtension.class)
class ExportControllerTest {

  @Mock private ExportService exportService;

  @Mock private MessageService messageService;

  @InjectMocks private ExportController exportController;

  @Test
  void startCollectCertificates() {
    exportController.startCollectCertificates();
    verify(exportService, times(1)).collectCertificatesToExport();
  }

  @Test
  void startCollectCertificateTexts() {
    exportController.startCollectCertificateTexts();
    verify(exportService, times(1)).collectCertificateTextsToExport();
  }

  @Test
  void startExportPackage() {
    exportController.startExportPackage();
    verify(exportService, times(1)).export();
  }

  @Test
  void sendPassword() {
    exportController.sendPasswords();
    verify(messageService, times(1)).sendPassword();
  }

  @Test
  void sendNotification() {
    exportController.sendNotification();
    verify(messageService, times(1)).sendNotification();
  }

  @Test
  void sendReminder() {
    exportController.sendReminder();
    verify(messageService, times(1)).sendReminder();
  }
}
