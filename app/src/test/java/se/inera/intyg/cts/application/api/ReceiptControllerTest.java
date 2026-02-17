package se.inera.intyg.cts.application.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.application.service.ReceiptService;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

    @Mock
    private ReceiptService receiptService;
    @InjectMocks
    private ReceiptController receiptController;

    @Test
    void handleReceipt() {
        UUID uuid = UUID.randomUUID();
        receiptController.handleReceipt(uuid);

        verify(receiptService, times(1)).handleReceipt(uuid);
    }
}