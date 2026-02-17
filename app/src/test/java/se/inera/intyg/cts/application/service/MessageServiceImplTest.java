package se.inera.intyg.cts.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.terminationWithNotificationTime;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.SendPackageNotification;
import se.inera.intyg.cts.domain.service.SendPackagePassword;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

  @Mock
  private TerminationRepository terminationRepository;
  @Mock
  private SendPackagePassword sendPackagePassword;
  @Mock
  private SendPackageNotification sendPackageNotification;

  @InjectMocks
  private MessageServiceImpl messageService;

  private final Termination termination1 = defaultTermination();
  private final Termination termination2 = defaultTermination();
  private final List<Termination> terminations = List.of(termination1, termination2);

  public static final int FOURTEEN_DAYS_IN_MINUTES = 20160;

  @Nested
  class TestSendPassword {

    @BeforeEach
    public void setFields() {
      setFieldValue("sendPasswordActive", true);
    }

    @Test
    void sendPassword() {
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);

      messageService.sendPassword();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verify(sendPackagePassword, times(2)).sendPassword(any(Termination.class));
    }

    @Test
    void sendPasswordForAllEvenIfOneFail() {
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);
      doThrow(new RuntimeException()).when(sendPackagePassword).sendPassword(termination1);

      messageService.sendPassword();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verify(sendPackagePassword, times(2)).sendPassword(any(Termination.class));
    }

    @Test
    void shouldNotSendPasswordWhenSendPasswordInactive() {
      setFieldValue("sendPasswordActive", false);
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);

      messageService.sendPassword();

      verifyNoInteractions(sendPackagePassword);
    }
  }

  @Nested
  class TestSendNotification {

    @BeforeEach
    public void setFields() {
      setFieldValue("sendNotificationActive", true);
    }

    @Test
    void sendNotification() {
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);

      messageService.sendNotification();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verify(sendPackageNotification, times(2)).sendNotification(any(Termination.class));
    }

    @Test
    void sendNotificationForAllEvenIfOneFail() {
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);
      doThrow(new RuntimeException()).when(sendPackageNotification).sendNotification(termination1);

      messageService.sendNotification();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verify(sendPackageNotification, times(2)).sendNotification(any(Termination.class));
    }

    @Test
    void shouldNotSendNotificationWhenSendNotificationInactive() {
      setFieldValue("sendNotificationActive", false);
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);

      messageService.sendNotification();

      verifyNoInteractions(sendPackageNotification);
    }
  }

  @Nested
  class TestSendReminder {

    @BeforeEach
    public void setFields() {
      setFieldValue("sendReminderActive", true);
      setFieldValue("reminderDelayInMinutes", FOURTEEN_DAYS_IN_MINUTES);
    }

    @Test
    void shouldSendReminderWhenPassedReminderTime() {
      final var notificationTime = LocalDateTime.now().minusDays(15L);
      final var termination = terminationWithNotificationTime(notificationTime);
      when(terminationRepository.findByStatuses(anyList())).thenReturn(List.of(termination));

      messageService.sendReminder();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verify(sendPackageNotification, times(1)).sendReminder(any(Termination.class));
    }

    @Test
    void sendReminderForAllEvenIfOneFail() {
      final var notificationTime = LocalDateTime.now().minusDays(15L);
      final var terminations = List.of(terminationWithNotificationTime(notificationTime),
          terminationWithNotificationTime(notificationTime));
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);
      doThrow(new RuntimeException()).when(sendPackageNotification).sendReminder(termination1);

      messageService.sendReminder();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verify(sendPackageNotification, times(2)).sendReminder(any(Termination.class));
    }

    @Test
    void shouldNotSendReminderWhenNotPassedReminderTime() {
      final var notificationTime = LocalDateTime.now().minusDays(13L);
      final var termination = terminationWithNotificationTime(notificationTime);
      when(terminationRepository.findByStatuses(anyList())).thenReturn(List.of(termination));

      messageService.sendReminder();

      verify(terminationRepository, times(1)).findByStatuses(anyList());
      verifyNoInteractions(sendPackageNotification);
    }

    @Test
    void shouldNotSendReminderWhenSendNotificationInactive() {
      setFieldValue("sendReminderActive", false);
      when(terminationRepository.findByStatuses(anyList())).thenReturn(terminations);

      messageService.sendReminder();

      verifyNoInteractions(sendPackageNotification);
    }
  }

  private <T> void setFieldValue(String field, T value) {
    ReflectionTestUtils.setField(messageService, field, value);
  }
}
