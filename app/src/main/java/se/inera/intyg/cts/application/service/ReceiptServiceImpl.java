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
package se.inera.intyg.cts.application.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  private static final Logger LOG = LoggerFactory.getLogger(ReceiptServiceImpl.class);

  private final TerminationRepository terminationRepository;

  public ReceiptServiceImpl(TerminationRepository terminationRepository) {
    this.terminationRepository = terminationRepository;
  }

  @Transactional
  public void handleReceipt(UUID terminationUUID) {
    LOG.info("Receipt received for termination id '{}'.", terminationUUID);
    final var receiptTime = LocalDateTime.now();
    final var terminationId = new TerminationId(terminationUUID);
    final var terminationOptional = terminationRepository.findByTerminationId(terminationId);

    final var termination =
        terminationOptional.orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        "Received receipt for non-existing terminationId '%s'.", terminationId)));

    termination.receiptReceived(receiptTime);
    terminationRepository.store(termination);
  }
}
