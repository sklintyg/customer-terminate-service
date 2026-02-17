package se.inera.intyg.cts.application.task;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.intyg.cts.application.service.ExportService;
import se.inera.intyg.cts.logging.MdcCloseableMap;
import se.inera.intyg.cts.logging.MdcHelper;
import se.inera.intyg.cts.logging.MdcLogConstants;

@Component
public class ExportTask {

  private static final String TASK_NAME = "ExportTask.run";
  private static final String LOCK_AT_MOST = "4m";
  private static final String LOCK_AT_LEAST = "4m";

  private final ExportService exportService;
  private final MdcHelper mdcHelper;

  public ExportTask(ExportService exportService, MdcHelper mdcHelper) {
    this.exportService = exportService;
    this.mdcHelper = mdcHelper;
  }

  @Scheduled(cron = "${task.export.cron}")
  @SchedulerLock(name = TASK_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
  public void collectCertificates() {
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(MdcLogConstants.TRACE_ID_KEY, mdcHelper.traceId())
            .put(MdcLogConstants.SPAN_ID_KEY, mdcHelper.spanId())
            .build()
    ) {
      exportService.export();
    }
  }
}
