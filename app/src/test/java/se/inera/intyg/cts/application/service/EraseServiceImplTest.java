package se.inera.intyg.cts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.EraseDataForCareProvider;

@ExtendWith(MockitoExtension.class)
class EraseServiceImplTest {

  @Mock
  private TerminationRepository terminationRepository;

  @Mock
  private EraseDataForCareProvider eraseDataForCareProvider;

  @InjectMocks
  private EraseServiceImpl eraseService;

  @Test
  void dontEraseIfNoTerminationOfCorrectStatus() {
    when(terminationRepository.findByStatuses(
        Arrays.asList(TerminationStatus.START_ERASE, TerminationStatus.ERASE_IN_PROGRESS)
    )).thenReturn(Collections.emptyList());

    eraseService.erase();

    verifyNoInteractions(eraseDataForCareProvider);
  }

  @Test
  void eraseWhenTerminationOfStatusStartErase() {
    final var termination = defaultTerminationBuilder()
        .status(TerminationStatus.START_ERASE)
        .create();

    when(terminationRepository.findByStatuses(
        Arrays.asList(TerminationStatus.START_ERASE, TerminationStatus.ERASE_IN_PROGRESS)
    )).thenReturn(Collections.singletonList(termination));

    eraseService.erase();

    verify(eraseDataForCareProvider, times(1)).erase(termination);
  }

  @Test
  void eraseWhenTerminationOfStatusEraseInProgress() {
    final var termination = defaultTerminationBuilder()
        .status(TerminationStatus.ERASE_IN_PROGRESS)
        .create();

    when(terminationRepository.findByStatuses(
        Arrays.asList(TerminationStatus.START_ERASE, TerminationStatus.ERASE_IN_PROGRESS)
    )).thenReturn(Collections.singletonList(termination));

    eraseService.erase();

    verify(eraseDataForCareProvider, times(1)).erase(termination);
  }

  @Test
  void setTerminationStatusToStartEraseWhenEraseIsInitiated() {
    final var termination = defaultTerminationBuilder()
        .status(TerminationStatus.PASSWORD_SENT)
        .create();

    when(terminationRepository.findByTerminationId(termination.terminationId()))
        .thenReturn(Optional.of(termination));

    final var terminationDTO = eraseService.initiateErase(termination.terminationId());

    assertEquals(TerminationStatus.START_ERASE.description(), terminationDTO.status());
    verify(terminationRepository, times(1)).store(termination);
  }

  @Test
  void throwExceptionIfTerminationDoesntExist() {
    final var termination = defaultTerminationBuilder()
        .status(TerminationStatus.PASSWORD_SENT)
        .create();

    when(terminationRepository.findByTerminationId(termination.terminationId()))
        .thenReturn(Optional.empty());

    final var id = termination.terminationId();
    assertThrows(IllegalArgumentException.class, () -> eraseService.initiateErase(id));

    verify(terminationRepository, times(0)).store(termination);
  }
}