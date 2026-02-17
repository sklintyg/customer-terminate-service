package se.inera.intyg.cts.application.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_NAME;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_EMAIL_ADDRESS;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_ORGANIZATION_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PERSON_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PHONE_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_TERMINATION_ID;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.inera.intyg.cts.application.dto.CreateTerminationDTO;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.application.dto.UpdateTerminationDTO;
import se.inera.intyg.cts.application.service.EraseService;
import se.inera.intyg.cts.application.service.TerminationService;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.testutil.TerminationTestDataBuilder;

@ExtendWith(MockitoExtension.class)
class TerminationControllerTest {

  @Mock
  private TerminationService terminationService;

  @Mock
  private EraseService eraseService;

  @InjectMocks
  private TerminationController terminationController;

  private final TerminationDTO terminationDTO = TerminationTestDataBuilder.defaultTerminationDTO();
  private final TerminationId terminationId = new TerminationId(DEFAULT_TERMINATION_ID);

  @Test
  void create() {
    CreateTerminationDTO request = new CreateTerminationDTO(DEFAULT_CREATOR_HSA_ID,
        DEFAULT_CREATOR_NAME,
        DEFAULT_HSA_ID, DEFAULT_ORGANIZATION_NUMBER, DEFAULT_PERSON_ID, DEFAULT_PHONE_NUMBER,
        DEFAULT_EMAIL_ADDRESS);
    when(terminationService.create(request)).thenReturn(terminationDTO);

    TerminationDTO terminationDTOResponse = terminationController.create(request);

    verify(terminationService, times(1)).create(request);
    assertEquals(terminationDTOResponse, terminationDTO);
  }

  @Test
  void createBadRequest() {
    CreateTerminationDTO request = new CreateTerminationDTO(DEFAULT_CREATOR_HSA_ID,
        DEFAULT_CREATOR_NAME,
        DEFAULT_HSA_ID, DEFAULT_ORGANIZATION_NUMBER, DEFAULT_PERSON_ID, DEFAULT_PHONE_NUMBER,
        DEFAULT_EMAIL_ADDRESS);

    when(terminationService.create(request)).thenThrow(IllegalArgumentException.class);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
        terminationController.create(request));

    verify(terminationService, times(1)).create(request);
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void update() {
    final var updateTerminationDTO = new UpdateTerminationDTO(
        "NewHSAId",
        "NewPersonId",
        "NewPhoneNumber",
        "NewEmailAddress"
    );

    when(terminationService.update(terminationDTO.terminationId(), updateTerminationDTO))
        .thenReturn(terminationDTO);

    final var terminationDTOResponse = terminationController.update(
        terminationDTO.terminationId(),
        updateTerminationDTO);

    assertEquals(terminationDTOResponse, terminationDTO);
  }

  @Test
  void updateBadRequest() {
    final var updateTerminationDTO = new UpdateTerminationDTO(
        "NewHSAId",
        "NewPersonId",
        "NewPhoneNumber",
        "NewEmailAddress"
    );
    when(terminationService.update(terminationDTO.terminationId(), updateTerminationDTO))
        .thenThrow(IllegalArgumentException.class);

    final var id = terminationDTO.terminationId();
    final var exception = assertThrows(ResponseStatusException.class, () ->
        terminationController.update(id, updateTerminationDTO)
    );

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void findById() {
    UUID uuid = UUID.randomUUID();
    when(terminationService.findById(uuid)).thenReturn(Optional.of(terminationDTO));

    TerminationDTO terminationDTOResponse = terminationController.findById(uuid);

    verify(terminationService, times(1)).findById(uuid);
    assertEquals(terminationDTOResponse, terminationDTO);
  }

  @Test
  void findByIdNotFound() {
    UUID uuid = UUID.randomUUID();
    when(terminationService.findById(uuid)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
        terminationController.findById(uuid));

    verify(terminationService, times(1)).findById(uuid);
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void findAll() {
    when(terminationService.findAll()).thenReturn(List.of(terminationDTO));

    final var terminationDTOResponse = terminationController.findAll();

    verify(terminationService, times(1)).findAll();
    assertEquals(terminationDTOResponse.get(0), terminationDTO);
  }

  @Test
  void erase() {
    when(eraseService.initiateErase(terminationId)).thenReturn(terminationDTO);

    final var terminationDTOResponse = terminationController.startErase(terminationId.id());

    assertEquals(terminationDTOResponse, terminationDTO);
  }

  @Test
  void eraseBadRequest() {
    when(eraseService.initiateErase(terminationId)).thenThrow(new IllegalArgumentException());

    final var id = terminationId.id();
    final var exception = assertThrows(ResponseStatusException.class, () ->
        terminationController.startErase(id));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Nested
  class ResendKey {

    @Test
    void resendKey() {
      when(terminationService.resendPassword(terminationId.id())).thenReturn(terminationDTO);

      TerminationDTO response = terminationController.resendPassword(terminationId.id());

      verify(terminationService, times(1)).resendPassword(terminationId.id());
      assertEquals(response, terminationDTO);
    }

    @Test
    void resendKeyNotFoundException() {
      when(terminationService.resendPassword(terminationId.id())).thenThrow(
          new NoSuchElementException());

      final var id = terminationId.id();
      final var exception = assertThrows(ResponseStatusException.class, () ->
          terminationController.resendPassword(id));

      verify(terminationService, times(1)).resendPassword(terminationId.id());
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void resendKeyIllegalArgumentException() {
      when(terminationService.resendPassword(terminationId.id())).thenThrow(
          new IllegalArgumentException());
      final var id = terminationId.id();
      final var exception = assertThrows(ResponseStatusException.class, () ->
          terminationController.resendPassword(id));

      verify(terminationService, times(1)).resendPassword(terminationId.id());
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
  }
}