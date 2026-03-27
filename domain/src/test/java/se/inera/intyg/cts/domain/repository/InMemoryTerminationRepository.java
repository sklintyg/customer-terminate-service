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
package se.inera.intyg.cts.domain.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;

public class InMemoryTerminationRepository implements TerminationRepository {

  private final Map<TerminationId, Termination> terminationMap = new HashMap<>();

  @Override
  public Termination store(Termination termination) {
    return terminationMap.put(termination.terminationId(), termination);
  }

  @Override
  public Optional<Termination> findByTerminationId(TerminationId id) {
    return terminationMap.containsKey(id) ? Optional.of(terminationMap.get(id)) : Optional.empty();
  }

  @Override
  public List<Termination> findAll() {
    return terminationMap.values().stream().toList();
  }

  @Override
  public List<Termination> findByStatuses(List<TerminationStatus> statuses) {
    return terminationMap.values().stream()
        .filter(termination -> statuses.contains(termination.status()))
        .collect(Collectors.toList());
  }
}
