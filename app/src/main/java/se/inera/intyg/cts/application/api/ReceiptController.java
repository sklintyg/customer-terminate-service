package se.inera.intyg.cts.application.api;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_CHANGE;
import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_INFO;

import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.cts.application.service.ReceiptService;
import se.inera.intyg.cts.logging.PerformanceLogging;

@RestController
@RequestMapping("/api/v1/receipt")
public class ReceiptController {

  private final ReceiptService receiptService;

  public ReceiptController(ReceiptService receiptService) {
    this.receiptService = receiptService;
  }

  @PostMapping("/{terminationUUID}")
  @PerformanceLogging(eventAction = "handle-receipt", eventType = EVENT_TYPE_CHANGE)
  void handleReceipt(@PathVariable UUID terminationUUID) {
    receiptService.handleReceipt(terminationUUID);
  }
}
