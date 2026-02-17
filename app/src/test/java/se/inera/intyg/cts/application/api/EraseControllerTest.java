package se.inera.intyg.cts.application.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.application.service.EraseService;

@ExtendWith(MockitoExtension.class)
class EraseControllerTest {

  @Mock
  private EraseService eraseService;

  @InjectMocks
  private EraseController eraseController;

  @Test
  void startErase() {
    eraseController.startErase();
    verify(eraseService, times(1)).erase();
  }
}