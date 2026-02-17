package se.inera.intyg.cts.application.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.CollectExportContent;
import se.inera.intyg.cts.domain.service.ExportPackage;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

  @Mock
  private TerminationRepository terminationRepository;

  @Mock
  private CollectExportContent collectExportContent;

  @Mock
  private ExportPackage exportPackage;

  @InjectMocks
  private ExportServiceImpl exportService;

  @Nested
  class CollectingCertificates {

    @Test
    void shallCollectCertificatesForTerminationWithStatusCreated() {
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.CREATED)
          .create();

      when(terminationRepository.findByStatuses(
          Arrays.asList(TerminationStatus.CREATED, TerminationStatus.COLLECTING_CERTIFICATES)))
          .thenReturn(Collections.singletonList(termination));

      exportService.collectCertificatesToExport();

      verify(collectExportContent, times(1)).collectCertificates(termination.terminationId());
    }

    @Test
    void shallCollectCertificatesForTerminationWithStatusCollectingCertificates() {
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES)
          .create();

      when(terminationRepository.findByStatuses(
          Arrays.asList(TerminationStatus.CREATED, TerminationStatus.COLLECTING_CERTIFICATES)))
          .thenReturn(Collections.singletonList(termination));

      exportService.collectCertificatesToExport();

      verify(collectExportContent, times(1)).collectCertificates(termination.terminationId());
    }

    @Test
    void shallCollectCertificatesForMultipleTerminationsIfExists() {
      final var terminationOne = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES)
          .terminationId(UUID.randomUUID())
          .create();

      final var terminationTwo = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES)
          .terminationId(UUID.randomUUID())
          .create();

      when(terminationRepository.findByStatuses(
          Arrays.asList(TerminationStatus.CREATED, TerminationStatus.COLLECTING_CERTIFICATES)))
          .thenReturn(Arrays.asList(terminationOne, terminationTwo));

      exportService.collectCertificatesToExport();

      verify(collectExportContent, times(1)).collectCertificates(terminationOne.terminationId());
      verify(collectExportContent, times(1)).collectCertificates(terminationTwo.terminationId());
    }

    @Test
    void shallCollectCertificatesForOtherEvenIfOneFails() {
      final var terminationOne = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES)
          .terminationId(UUID.randomUUID())
          .create();

      final var terminationTwo = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES)
          .terminationId(UUID.randomUUID())
          .create();

      when(terminationRepository.findByStatuses(
          Arrays.asList(TerminationStatus.CREATED, TerminationStatus.COLLECTING_CERTIFICATES)))
          .thenReturn(Arrays.asList(terminationOne, terminationTwo));

      doThrow(new RuntimeException("Something went wrong!"))
          .when(collectExportContent).collectCertificates(terminationOne.terminationId());

      exportService.collectCertificatesToExport();

      verify(collectExportContent, times(1)).collectCertificates(terminationOne.terminationId());
      verify(collectExportContent, times(1)).collectCertificates(terminationTwo.terminationId());
    }
  }

  @Nested
  class CollectingCertificateTexts {

    @Test
    void shallCollectCertificateTextsForTerminationWithStatusCollectingCertificatesCompleted() {
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)
          .create();

      when(terminationRepository.findByStatuses(
          Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)))
          .thenReturn(Collections.singletonList(termination));

      exportService.collectCertificateTextsToExport();

      verify(collectExportContent, times(1)).collectCertificateTexts(termination);
    }

    @Test
    void shallCollectCertificateTextsForMultipleTerminationsIfExists() {
      final var terminationOne = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      final var terminationTwo = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      when(terminationRepository.findByStatuses(
          Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)))
          .thenReturn(Arrays.asList(terminationOne, terminationTwo));

      exportService.collectCertificateTextsToExport();

      verify(collectExportContent, times(1)).collectCertificateTexts(terminationOne);
      verify(collectExportContent, times(1)).collectCertificateTexts(terminationTwo);
    }

    @Test
    void shallCollectCertificateTextsForOtherEvenIfOneFails() {
      final var terminationOne = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      final var terminationTwo = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      when(terminationRepository.findByStatuses(
          Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED)))
          .thenReturn(Arrays.asList(terminationOne, terminationTwo));

      doThrow(new RuntimeException("Something went wrong!"))
          .when(collectExportContent).collectCertificateTexts(terminationOne);

      exportService.collectCertificateTextsToExport();

      verify(collectExportContent, times(1)).collectCertificateTexts(terminationOne);
      verify(collectExportContent, times(1)).collectCertificateTexts(terminationTwo);
    }
  }

  @Nested
  class Export {

    @Test
    void shallCollectCertificateTextsForTerminationWithStatusCollectingCertificatesCompleted() {
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)
          .create();

      when(terminationRepository.findByStatuses(
          Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)))
          .thenReturn(Collections.singletonList(termination));

      exportService.export();

      verify(exportPackage, times(1)).export(termination);
    }

    @Test
    void shallCollectCertificateTextsForMultipleTerminationsIfExists() {
      final var terminationOne = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      final var terminationTwo = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      when(terminationRepository.findByStatuses(
          Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)))
          .thenReturn(Arrays.asList(terminationOne, terminationTwo));

      exportService.export();

      verify(exportPackage, times(1)).export(terminationOne);
      verify(exportPackage, times(1)).export(terminationTwo);
    }

    @Test
    void shallCollectCertificateTextsForOtherEvenIfOneFails() {
      final var terminationOne = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      final var terminationTwo = defaultTerminationBuilder()
          .status(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)
          .terminationId(UUID.randomUUID())
          .create();

      when(terminationRepository.findByStatuses(
          Collections.singletonList(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED)))
          .thenReturn(Arrays.asList(terminationOne, terminationTwo));

      doThrow(new RuntimeException("Something went wrong!"))
          .when(exportPackage).export(terminationOne);

      exportService.export();

      verify(exportPackage, times(1)).export(terminationOne);
      verify(exportPackage, times(1)).export(terminationTwo);
    }
  }
}