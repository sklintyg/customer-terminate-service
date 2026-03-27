/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.application.api;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_CHANGE;
import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_INFO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.cts.application.service.ExportService;
import se.inera.intyg.cts.application.service.MessageService;
import se.inera.intyg.cts.logging.PerformanceLogging;

@RestController
@RequestMapping("/api/v1/exports")
public class ExportController {

  private static final Logger LOG = LoggerFactory.getLogger(ExportController.class);

  private final ExportService exportService;
  private final MessageService messageService;

  public ExportController(ExportService exportService, MessageService messageService) {
    this.exportService = exportService;
    this.messageService = messageService;
  }

  @PostMapping("/collectCertificates")
  @PerformanceLogging(eventAction = "collect-certificates", eventType = EVENT_TYPE_CHANGE)
  void startCollectCertificates() {
    LOG.info("Start collecting certificates");
    exportService.collectCertificatesToExport();
  }

  @PostMapping("/collectCertificateTexts")
  @PerformanceLogging(eventAction = "collect-certificates-texts", eventType = EVENT_TYPE_CHANGE)
  void startCollectCertificateTexts() {
    LOG.info("Start collecting certificate texts");
    exportService.collectCertificateTextsToExport();
  }

  @PostMapping("/exportPackage")
  @PerformanceLogging(eventAction = "export-package", eventType = EVENT_TYPE_CHANGE)
  void startExportPackage() {
    LOG.info("Start export package");
    exportService.export();
  }

  @PostMapping("/sendPasswords")
  @PerformanceLogging(eventAction = "send-password", eventType = EVENT_TYPE_INFO)
  void sendPasswords() {
    LOG.info("Start send passwords");
    messageService.sendPassword();
  }

  @PostMapping("/sendNotification")
  @PerformanceLogging(eventAction = "send-notification", eventType = EVENT_TYPE_INFO)
  void sendNotification() {
    LOG.info("Start send notification");
    messageService.sendNotification();
  }

  @PostMapping("/sendReminder")
  @PerformanceLogging(eventAction = "send-reminder", eventType = EVENT_TYPE_INFO)
  void sendReminder() {
    LOG.info("Start send reminder");
    messageService.sendReminder();
  }
}
