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
