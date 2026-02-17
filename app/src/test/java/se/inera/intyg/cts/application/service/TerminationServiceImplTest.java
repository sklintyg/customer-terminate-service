package se.inera.intyg.cts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.application.dto.TerminationDTOMapper.toDTO;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_NAME;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_EMAIL_ADDRESS;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_ORGANIZATION_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PERSON_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PHONE_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_TERMINATION_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import java.util.ArrayList;
import java.util.Optional;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.application.dto.CreateTerminationDTO;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.application.dto.UpdateTerminationDTO;
import se.inera.intyg.cts.domain.model.EmailAddress;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.PersonId;
import se.inera.intyg.cts.domain.model.PhoneNumber;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.SendPackagePassword;
import se.inera.intyg.cts.domain.service.UpdateTermination;

@ExtendWith(MockitoExtension.class)
class TerminationServiceImplTest {

  @Mock
  private UpdateTermination updateTermination;
  @Mock
  private SendPackagePassword sendPackagePassword;
  @Mock
  private TerminationRepository terminationRepository;

  @Captor
  ArgumentCaptor<Termination> terminationArgumentCaptor;

  private TerminationServiceImpl terminationServiceImpl;
  private Termination termination;

  @BeforeEach
  void setUp() {
    terminationServiceImpl = new TerminationServiceImpl(terminationRepository, sendPackagePassword,
        updateTermination);

    termination = defaultTermination();
  }

  @Nested
  class CreateTermination {

    private CreateTerminationDTO createTerminationDTO;

    @BeforeEach
    void setUp() {
      createTerminationDTO = new CreateTerminationDTO(DEFAULT_CREATOR_HSA_ID, DEFAULT_CREATOR_NAME,
          DEFAULT_HSA_ID, DEFAULT_ORGANIZATION_NUMBER, DEFAULT_PERSON_ID, DEFAULT_PHONE_NUMBER,
          DEFAULT_EMAIL_ADDRESS);
      when(terminationRepository.store(any(Termination.class))).thenReturn(termination);
    }

    @Test
    void shallCreateTermination() {
      assertNotNull(terminationServiceImpl.create(createTerminationDTO), "Termination is null");

      verify(terminationRepository, times(1)).store(any(Termination.class));
    }

    @Test
    void shallCreateTerminationWithTerminationId() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertNotNull(terminationArgumentCaptor.getValue().terminationId(),
          "TerminationId is null");

    }

    @Test
    void shallCreateTerminationWithCreatorHSAId() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_CREATOR_HSA_ID,
          terminationArgumentCaptor.getValue().creator().hsaId().id());
    }

    @Test
    void shallCreateTerminationWithCreatorName() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_CREATOR_NAME,
          terminationArgumentCaptor.getValue().creator().name());
    }

    @Test
    void shallCreateTerminationWithHSAId() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_HSA_ID,
          terminationArgumentCaptor.getValue().careProvider().hsaId().id());
    }

    @Test
    void shallCreateTerminationWithOrganizationNumber() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_ORGANIZATION_NUMBER,
          terminationArgumentCaptor.getValue().careProvider().organizationNumber().number());
    }

    @Test
    void shallCreateTerminationWithPersonId() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_PERSON_ID,
          terminationArgumentCaptor.getValue().export().organizationRepresentative().personId()
              .id());
    }

    @Test
    void shallCreateTerminationWithPhoneNumber() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_PHONE_NUMBER,
          terminationArgumentCaptor.getValue().export().organizationRepresentative().phoneNumber()
              .number());
    }

    @Test
    void shallCreateTerminationWithEmailAddress() {
      terminationServiceImpl.create(createTerminationDTO);
      verify(terminationRepository).store(terminationArgumentCaptor.capture());
      assertEquals(DEFAULT_EMAIL_ADDRESS,
          terminationArgumentCaptor.getValue().export().organizationRepresentative().emailAddress()
              .emailAddress());
    }
  }

  @Test
  void shallReturnExistingTermination() {
    when(terminationRepository.findByTerminationId(any(TerminationId.class))).thenReturn(
        Optional.of(termination));

    assertTrue(terminationServiceImpl.findById(DEFAULT_TERMINATION_ID).isPresent(),
        "Shall contain a termination");

    verify(terminationRepository, times(1)).findByTerminationId(any(TerminationId.class));
  }

  @Test
  void shallReturnEmptyOptionalWhenTerminationIdDoesntExist() {
    when(terminationRepository.findByTerminationId(any(TerminationId.class))).thenReturn(
        Optional.empty());

    assertTrue(terminationServiceImpl.findById(DEFAULT_TERMINATION_ID).isEmpty(),
        "Shall not contain any termination");

    verify(terminationRepository, times(1)).findByTerminationId(any(TerminationId.class));
  }

  @Test
  void shallReturnAllExistingTerminations() {
    final var numberOfTerminations = 10;
    final var terminations = new ArrayList<Termination>();
    for (int i = 0; i < numberOfTerminations; i++) {
      terminations.add(defaultTermination());
    }
    when(terminationRepository.findAll()).thenReturn(terminations);

    assertEquals(numberOfTerminations, terminationServiceImpl.findAll().size());
  }

  @Nested
  class ResendKey {

    @BeforeEach
    void setUp() {
      terminationServiceImpl = new TerminationServiceImpl(terminationRepository,
          sendPackagePassword, updateTermination);
    }

    @Test
    void resendKey() {
      terminationServiceImpl = new TerminationServiceImpl(terminationRepository,
          sendPackagePassword, updateTermination);
      when(terminationRepository.findByTerminationId(any(TerminationId.class))).thenReturn(
          Optional.of(termination));
      when(sendPackagePassword.resendPassword(termination)).thenReturn(termination);

      assertNotNull(terminationServiceImpl.resendPassword(termination.terminationId().id()));

      verify(terminationRepository, times(1)).findByTerminationId(any(TerminationId.class));
      verify(sendPackagePassword, times(1)).resendPassword(termination);
    }

    @Test
    void resendKeyNotFound() throws ObjectNotFoundException {
      when(terminationRepository.findByTerminationId(any(TerminationId.class))).thenReturn(
          Optional.empty());

      final var id = termination.terminationId().id();
      assertThrows(IllegalArgumentException.class, () -> terminationServiceImpl.resendPassword(id));

      verify(terminationRepository, times(1)).findByTerminationId(any(TerminationId.class));
      verify(sendPackagePassword, times(0)).resendPassword(termination);
    }
  }

  @Nested
  class UpdateTerminationMetadata {

    private Termination termination;
    private TerminationDTO terminationDTO;
    private UpdateTerminationDTO updateTerminationDTO;

    @BeforeEach
    void setUp() {
      termination = defaultTermination();
      terminationDTO = toDTO(termination);
      updateTerminationDTO = new UpdateTerminationDTO(
          terminationDTO.hsaId(),
          terminationDTO.personId(),
          terminationDTO.phoneNumber(),
          terminationDTO.emailAddress()
      );
    }

    @Test
    void shallUpdateTermination() {
      when(terminationRepository.findByTerminationId(any(TerminationId.class))).thenReturn(
          Optional.of(termination));
      when(updateTermination.update(
          any(Termination.class),
          eq(new HSAId(terminationDTO.hsaId())),
          eq(new PersonId(terminationDTO.personId())),
          eq(new EmailAddress(terminationDTO.emailAddress())),
          eq(new PhoneNumber(terminationDTO.phoneNumber())))).thenReturn(termination);

      final var updatedTermination = terminationServiceImpl.update(terminationDTO.terminationId(),
          updateTerminationDTO);
      assertNotNull(updatedTermination, "Termination is null");
    }

    @Test
    void shallThrowExceptionIfTerminationDoesntExists() {
      when(terminationRepository.findByTerminationId(any(TerminationId.class))).thenReturn(
          Optional.empty());
      final var id = terminationDTO.terminationId();
      final var exception = assertThrows(IllegalArgumentException.class, () ->
          terminationServiceImpl.update(id, updateTerminationDTO));

      assertTrue(exception.getMessage().contains("doesn't exist!"));
    }
  }

}