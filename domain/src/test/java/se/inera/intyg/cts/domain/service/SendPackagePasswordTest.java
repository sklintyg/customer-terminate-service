package se.inera.intyg.cts.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

@ExtendWith(MockitoExtension.class)
class SendPackagePasswordTest {

  @Mock
  private SendPassword sendPassword;

  @Mock
  private TerminationRepository terminationRepository;

  @InjectMocks
  private SendPackagePasswordImpl sendPackagePassword;

  @Mock
  private Termination termination1;
  @Mock
  private Termination termination2;

  private TerminationId terminationId1 = new TerminationId(UUID.randomUUID());
  private TerminationId terminationId2 = new TerminationId(UUID.randomUUID());

  @Nested
  class sendPasswordTest {
    @Test
    public void shouldUpdateTerminationWhenSuccessfulPassword() {
      when(sendPassword.sendPassword(termination1)).thenReturn(true);
      when(terminationRepository.findByTerminationId(termination1.terminationId())).thenReturn(Optional.of(termination1));

      sendPackagePassword.sendPassword(termination1);

      verify(sendPassword, times(1)).sendPassword(termination1);
      verify(termination1, times(1)).passwordSent();
      verify(terminationRepository, times(1)).store(termination1);
      verify(terminationRepository, times(1)).findByTerminationId(termination1.terminationId());
    }

    @Test
    public void shouldNotUpdateTerminationWhenFailedPassword() {
      when(sendPassword.sendPassword(termination1)).thenReturn(false);

      sendPackagePassword.sendPassword(termination1);

      verify(sendPassword, times(1)).sendPassword(termination1);
      verify(termination1, times(0)).passwordSent();
      verify(terminationRepository, times(0)).store(termination1);
      verify(terminationRepository, times(1)).findByTerminationId(termination1.terminationId());
    }
  }

  @Nested
  class resendPasswordTest {

    @Test
    public void resendPassword() {
      when(termination1.status()).thenReturn(TerminationStatus.PASSWORD_SENT);
      when(termination2.status()).thenReturn(TerminationStatus.PASSWORD_RESENT);
      when(termination1.terminationId()).thenReturn(terminationId1);
      when(termination2.terminationId()).thenReturn(terminationId2);
      when(sendPassword.sendPassword(termination1)).thenReturn(true);
      when(sendPassword.sendPassword(termination2)).thenReturn(true);
      when(terminationRepository.findByTerminationId(terminationId1)).thenReturn(Optional.of(termination1));
      when(terminationRepository.findByTerminationId(terminationId2)).thenReturn(Optional.of(termination2));

      assertEquals(sendPackagePassword.resendPassword(termination1), termination1);
      assertEquals(sendPackagePassword.resendPassword(termination2), termination2);

      verify(termination1, times(1)).status();
      verify(termination2, times(2)).status();
      verify(sendPassword, times(1)).sendPassword(termination1);
      verify(sendPassword, times(1)).sendPassword(termination2);
      verify(termination1, times(1)).passwordResent();
      verify(termination2, times(1)).passwordResent();
      verify(termination1, times(1)).terminationId();
      verify(termination2, times(1)).terminationId();
      verify(terminationRepository, times(1)).store(termination1);
      verify(terminationRepository, times(1)).store(termination2);
      verify(terminationRepository, times(1)).findByTerminationId(terminationId1);
      verify(terminationRepository, times(1)).findByTerminationId(terminationId2);
    }

    @Test
    public void resendPasswordRuntimeException() {
      when(termination1.status()).thenReturn(TerminationStatus.PASSWORD_RESENT);
      when(sendPassword.sendPassword(termination1)).thenReturn(false);

      assertThrows(RuntimeException.class, () -> {
        sendPackagePassword.resendPassword(termination1);
      });

      verify(termination1, times(2)).status();
      verify(sendPassword, times(1)).sendPassword(termination1);
      verify(termination1, times(0)).passwordResent();
      verify(terminationRepository, times(0)).store(termination1);
    }

    @Test
    public void resendPasswordIllegalArgumentException() {
      when(termination1.status()).thenReturn(TerminationStatus.CREATED);

      assertThrows(IllegalArgumentException.class, () -> {
        sendPackagePassword.resendPassword(termination1);
      });

      verify(termination1, times(3)).status();
      verify(sendPassword, times(0)).sendPassword(termination1);
      verify(termination1, times(0)).passwordResent();
      verify(terminationRepository, times(0)).store(termination1);
    }
  }
}
