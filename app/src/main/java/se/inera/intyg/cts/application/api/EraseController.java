package se.inera.intyg.cts.application.api;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_DELETION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.cts.application.service.EraseService;
import se.inera.intyg.cts.logging.PerformanceLogging;

@RestController
@RequestMapping("/api/v1/erase")
public class EraseController {

  private Logger LOG = LoggerFactory.getLogger(EraseController.class);
  
  private final EraseService eraseService;

  public EraseController(EraseService eraseService) {
    this.eraseService = eraseService;
  }

  @PostMapping("")
  @PerformanceLogging(eventAction = "start-erase", eventType = EVENT_TYPE_DELETION)
  void startErase() {
    LOG.info("Start erase");
    eraseService.erase();
  }
}
