package se.inera.intyg.cts.domain.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.DEFAULT_REVOKED;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.DEFAULT_TOTAL;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.exportedTerminationWithEraseInProgress;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.exportedTerminationWithStartErase;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.CertificateSummary;
import se.inera.intyg.cts.domain.model.EraseService;
import se.inera.intyg.cts.domain.model.ServiceId;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.CertificateBatchRepository;
import se.inera.intyg.cts.domain.repository.InMemoryTerminationRepository;

@ExtendWith(MockitoExtension.class)
class EraseDataForCareProviderTest {

  private final InMemoryTerminationRepository inMemoryTerminationRepository = new InMemoryTerminationRepository();

  @Mock
  private CertificateBatchRepository certificateBatchRepository;

  private EraseDataForCareProvider eraseDataForCareProvider;

  private List<EraseDataInService> eraseDataInServices = new ArrayList<>();

  @BeforeEach
  void setUp() {
    eraseDataForCareProvider = new EraseDataForCareProviderImpl(
        eraseDataInServices,
        certificateBatchRepository,
        inMemoryTerminationRepository
    );
  }

  @Nested
  class SingleServiceErase {

    @Mock
    private EraseDataInService eraseDataInService;

    private static final String SERVICE_ID = "SERVICE_ID";

    @Nested
    class StartErase {

      private final Termination termination = exportedTerminationWithStartErase();

      @BeforeEach
      void setUp() {
        eraseDataInServices.add(eraseDataInService);

        doReturn(new ServiceId(SERVICE_ID))
            .when(eraseDataInService)
            .serviceId();

        doReturn(new CertificateSummary(DEFAULT_TOTAL, DEFAULT_REVOKED))
            .when(certificateBatchRepository)
            .certificateSummary(termination);

        inMemoryTerminationRepository.store(termination);
      }

      @Test
      void shallSetTerminationInEraseInProgressIfExportedCertificateSummaryHasNotChanged() {
        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_IN_PROGRESS,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallSetEraseServicesIfExportedCertificateSummaryHasNotChanged() {
        eraseDataForCareProvider.erase(termination);

        assertAll(
            () -> assertEquals(new ServiceId(SERVICE_ID),
                termination(termination.terminationId()).erase().eraseServices().get(0)
                    .serviceId()),
            () -> assertEquals(false,
                termination(termination.terminationId()).erase().eraseServices().get(0).erased())
        );
      }
    }

    @Nested
    class StartEraseFail {

      private final Termination termination = exportedTerminationWithStartErase();

      @BeforeEach
      void setUp() {
        inMemoryTerminationRepository.store(termination);
      }

      @Test
      void shallSetTerminationInFailedEraseStatusIfTotalCertificatesChanged() {
        doReturn(new CertificateSummary(DEFAULT_TOTAL + 10, DEFAULT_REVOKED))
            .when(certificateBatchRepository)
            .certificateSummary(termination);

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_CANCELLED,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallSetTerminationInFailedEraseStatusIfRevokedCertificatesChanged() {
        doReturn(new CertificateSummary(DEFAULT_TOTAL, DEFAULT_REVOKED + 1))
            .when(certificateBatchRepository)
            .certificateSummary(termination);

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_CANCELLED,
            termination(termination.terminationId()).status());
      }
    }

    @Nested
    class EraseOngoing {

      private final List<EraseService> eraseServices = List.of(
          new EraseService(new ServiceId(SERVICE_ID), false)
      );

      private final Termination termination = exportedTerminationWithEraseInProgress(eraseServices);

      @BeforeEach
      void setUp() {
        eraseDataInServices.add(eraseDataInService);
      }

      @Test
      void shallSetEraseServiceAsErasedWhenSuccessful() {
        doReturn(new ServiceId(SERVICE_ID))
            .when(eraseDataInService)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        assertTrue(
            termination(termination.terminationId()).erase().eraseServices().get(0).erased());
      }

      @Test
      void shallLeaveEraseServiceAsNotErasedWhenThrowsException() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID))
            .when(eraseDataInService)
            .serviceId();

        doThrow(new EraseException("Failed to erase!"))
            .when(eraseDataInService)
            .erase(termination);

        eraseDataForCareProvider.erase(termination);

        assertFalse(
            termination(termination.terminationId()).erase().eraseServices().get(0).erased());
      }

      @Test
      void shallSetTerminationInEraseCompletedWhenAllServicesErased() {
        doReturn(new ServiceId(SERVICE_ID))
            .when(eraseDataInService)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_COMPLETED,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallEraseDataInService() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID))
            .when(eraseDataInService)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        verify(eraseDataInService)
            .erase(termination);
      }
    }
  }

  @Nested
  class MultipleServiceErase {

    @Mock
    private EraseDataInService eraseDataInServiceOne;

    @Mock
    private EraseDataInService eraseDataInServiceTwo;

    @Mock
    private EraseDataInService eraseDataInServiceThree;

    private final static String SERVICE_ID_ONE = "SERVICE_ID_ONE";
    private final static String SERVICE_ID_TWO = "SERVICE_ID_TWO";
    private final static String SERVICE_ID_THREE = "SERVICE_ID_THREE";

    @Nested
    class StartErase {

      private final List<EraseService> eraseServices = List.of(
          new EraseService(new ServiceId(SERVICE_ID_ONE), false),
          new EraseService(new ServiceId(SERVICE_ID_TWO), false),
          new EraseService(new ServiceId(SERVICE_ID_THREE), false)
      );

      private final Termination termination = exportedTerminationWithStartErase();

      @BeforeEach
      void setUp() {
        eraseDataInServices.add(eraseDataInServiceOne);
        eraseDataInServices.add(eraseDataInServiceTwo);
        eraseDataInServices.add(eraseDataInServiceThree);

        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        doReturn(new CertificateSummary(DEFAULT_TOTAL, DEFAULT_REVOKED))
            .when(certificateBatchRepository)
            .certificateSummary(termination);

        inMemoryTerminationRepository.store(termination);
      }

      @Test
      void shallSetTerminationInEraseInProgressIfExportedCertificateSummaryHasNotChanged() {
        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_IN_PROGRESS,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallSetEraseServicesIfExportedCertificateSummaryHasNotChanged() {
        eraseDataForCareProvider.erase(termination);

        assertAll(
            () -> assertEquals(new ServiceId(SERVICE_ID_ONE),
                termination(termination.terminationId()).erase().eraseServices().get(0)
                    .serviceId()),
            () -> assertEquals(false,
                termination(termination.terminationId()).erase().eraseServices().get(0).erased()),
            () -> assertEquals(new ServiceId(SERVICE_ID_TWO),
                termination(termination.terminationId()).erase().eraseServices().get(1)
                    .serviceId()),
            () -> assertEquals(false,
                termination(termination.terminationId()).erase().eraseServices().get(1).erased()),
            () -> assertEquals(new ServiceId(SERVICE_ID_THREE),
                termination(termination.terminationId()).erase().eraseServices().get(2)
                    .serviceId()),
            () -> assertEquals(false,
                termination(termination.terminationId()).erase().eraseServices().get(2).erased())
        );
      }
    }

    @Nested
    class EraseOngoing {

      private final List<EraseService> eraseServices = List.of(
          new EraseService(new ServiceId(SERVICE_ID_ONE), false),
          new EraseService(new ServiceId(SERVICE_ID_TWO), false),
          new EraseService(new ServiceId(SERVICE_ID_THREE), false)
      );

      private final Termination termination = exportedTerminationWithEraseInProgress(eraseServices);

      @BeforeEach
      void setUp() {
        eraseDataInServices.add(eraseDataInServiceOne);
        eraseDataInServices.add(eraseDataInServiceTwo);
        eraseDataInServices.add(eraseDataInServiceThree);
      }

      @Test
      void shallSetEraseServiceAsErasedWhenSuccessful() {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        assertAll(
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(0).erased()),
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(1).erased()),
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(2).erased())
        );
      }

      @Test
      void shallLeaveEraseServiceAsNotErasedWhenThrowsException() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        doThrow(new EraseException("Failed to erase!"))
            .when(eraseDataInServiceTwo)
            .erase(termination);

        eraseDataForCareProvider.erase(termination);

        assertAll(
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(0).erased()),
            () -> assertFalse(
                termination(termination.terminationId()).erase().eraseServices().get(1).erased()),
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(2).erased())
        );
      }

      @Test
      void shallLeaveTerminationInEraseInProgressWhenPartOfServicesErased() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        doThrow(new EraseException("Failed to erase!"))
            .when(eraseDataInServiceTwo)
            .erase(termination);

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_IN_PROGRESS,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallSetTerminationInEraseCompletedWhenAllServicesErased() {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_COMPLETED,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallEraseDataInService() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        verify(eraseDataInServiceOne)
            .erase(termination);
        verify(eraseDataInServiceTwo)
            .erase(termination);
        verify(eraseDataInServiceThree)
            .erase(termination);
      }
    }

    @Nested
    class EraseOngoingWithSomeServicesComplete {

      private final List<EraseService> eraseServices = List.of(
          new EraseService(new ServiceId(SERVICE_ID_ONE), true),
          new EraseService(new ServiceId(SERVICE_ID_TWO), false),
          new EraseService(new ServiceId(SERVICE_ID_THREE), true)
      );

      private final Termination termination = exportedTerminationWithEraseInProgress(eraseServices);

      @BeforeEach
      void setUp() {
        eraseDataInServices.add(eraseDataInServiceOne);
        eraseDataInServices.add(eraseDataInServiceTwo);
        eraseDataInServices.add(eraseDataInServiceThree);
      }

      @Test
      void shallSetEraseServiceAsErasedWhenSuccessful() {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        assertAll(
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(0).erased()),
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(1).erased()),
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(2).erased())
        );
      }

      @Test
      void shallLeaveEraseServiceAsNotErasedWhenThrowsException() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        doThrow(new EraseException("Failed to erase!"))
            .when(eraseDataInServiceTwo)
            .erase(termination);

        eraseDataForCareProvider.erase(termination);

        assertAll(
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(0).erased()),
            () -> assertFalse(
                termination(termination.terminationId()).erase().eraseServices().get(1).erased()),
            () -> assertTrue(
                termination(termination.terminationId()).erase().eraseServices().get(2).erased())
        );
      }

      @Test
      void shallLeaveTerminationInEraseInProgressWhenPartOfServicesErased() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        doThrow(new EraseException("Failed to erase!"))
            .when(eraseDataInServiceTwo)
            .erase(termination);

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_IN_PROGRESS,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallSetTerminationInEraseCompletedWhenAllServicesErased() {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        assertEquals(TerminationStatus.ERASE_COMPLETED,
            termination(termination.terminationId()).status());
      }

      @Test
      void shallEraseDataInService() throws EraseException {
        doReturn(new ServiceId(SERVICE_ID_ONE))
            .when(eraseDataInServiceOne)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_TWO))
            .when(eraseDataInServiceTwo)
            .serviceId();

        doReturn(new ServiceId(SERVICE_ID_THREE))
            .when(eraseDataInServiceThree)
            .serviceId();

        eraseDataForCareProvider.erase(termination);

        verify(eraseDataInServiceOne, never())
            .erase(termination);
        verify(eraseDataInServiceTwo)
            .erase(termination);
        verify(eraseDataInServiceThree, never())
            .erase(termination);
      }
    }
  }

  private Termination termination(TerminationId terminationId) {
    return inMemoryTerminationRepository.findByTerminationId(terminationId).orElseThrow();
  }
}