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

  @Mock
  private ExportService exportService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private ExportController exportController;

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