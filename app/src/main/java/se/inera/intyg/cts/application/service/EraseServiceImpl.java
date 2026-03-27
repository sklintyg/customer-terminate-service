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

import static se.inera.intyg.cts.application.dto.TerminationDTOMapper.toDTO;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.EraseDataForCareProvider;

@Service
public class EraseServiceImpl implements EraseService {

  private static final Logger LOG = LoggerFactory.getLogger(EraseServiceImpl.class);

  private final TerminationRepository terminationRepository;
  private final EraseDataForCareProvider eraseDataForCareProvider;

  public EraseServiceImpl(
      TerminationRepository terminationRepository,
      EraseDataForCareProvider eraseDataForCareProvider) {
    this.terminationRepository = terminationRepository;
    this.eraseDataForCareProvider = eraseDataForCareProvider;
  }

  @Override
  public void erase() {
    final var terminations =
        terminationRepository.findByStatuses(
            Arrays.asList(TerminationStatus.START_ERASE, TerminationStatus.ERASE_IN_PROGRESS));

    terminations.forEach(
        termination -> {
          eraseDataForCareProvider.erase(termination);
          if (termination.status() == TerminationStatus.ERASE_COMPLETED) {
            LOG.info("Erase completed for termination '{}'", termination.terminationId().id());
          }
        });
  }

  @Override
  public TerminationDTO initiateErase(TerminationId terminationId) {
    final var termination =
        terminationRepository
            .findByTerminationId(terminationId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format(
                            "Termination with id '%s' doesn't exists.", terminationId.id())));

    termination.initiateErase();
    terminationRepository.store(termination);
    LOG.info("Initiated erase of termination '{}'", termination.terminationId().id());

    return toDTO(termination);
  }
}
