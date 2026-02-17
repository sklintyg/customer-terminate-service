package se.inera.intyg.cts.application.task;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.application.service.MessageService;
import se.inera.intyg.cts.logging.MdcHelper;

@ExtendWith(MockitoExtension.class)
class SendNotificationTaskTest {

  @Mock
  private MdcHelper mdcHelper;

  @Mock
  MessageService messageService;

  @InjectMocks
  SendNotificationTask sendNotificationTask;

  @BeforeEach
  void setUp() {
    doReturn("traceId").when(mdcHelper).traceId();
    doReturn("spanId").when(mdcHelper).spanId();
  }

  @Test
  void testSendExportedPackageNotification() {
    sendNotificationTask.sendNotification();
    verify(messageService, times(1)).sendNotification();
  }
}