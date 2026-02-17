package se.inera.intyg.cts.application.task;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.intyg.cts.application.service.EraseService;
import se.inera.intyg.cts.logging.MdcCloseableMap;
import se.inera.intyg.cts.logging.MdcHelper;
import se.inera.intyg.cts.logging.MdcLogConstants;

@Component
public class EraseCareProviderTask {

  private static final String TASK_NAME = "EraseCareProviderTask.run";
  private static final String LOCK_AT_MOST = "PT50S";
  private static final String LOCK_AT_LEAST = "PT50S";

  private final EraseService eraseService;
  private final MdcHelper mdcHelper;

  public EraseCareProviderTask(EraseService eraseService, MdcHelper mdcHelper) {
    this.eraseService = eraseService;
    this.mdcHelper = mdcHelper;
  }

  @Scheduled(cron = "${task.erasecareprovider.cron}")
  @SchedulerLock(name = TASK_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
  public void eraseCareProvider() {
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(MdcLogConstants.TRACE_ID_KEY, mdcHelper.traceId())
            .put(MdcLogConstants.SPAN_ID_KEY, mdcHelper.spanId())
            .build()
    ) {
      eraseService.erase();
    }
  }
}
