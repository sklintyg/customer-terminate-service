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
package se.inera.intyg.cts.infrastructure.persistence;

import static se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper.toDomain;
import static se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper.toEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@Repository
public class JpaTerminationRepository implements TerminationRepository {

  private final TerminationEntityRepository terminationEntityRepository;

  public JpaTerminationRepository(TerminationEntityRepository terminationEntityRepository) {
    this.terminationEntityRepository = terminationEntityRepository;
  }

  @Override
  public Termination store(Termination termination) {
    final var terminationEntity = toEntity(termination);

    terminationEntityRepository
        .findByTerminationId(termination.terminationId().id())
        .ifPresent(entity -> terminationEntity.setId(entity.getId()));

    final var savedTerminationEntity = terminationEntityRepository.save(terminationEntity);
    return toDomain(savedTerminationEntity);
  }

  @Override
  public Optional<Termination> findByTerminationId(TerminationId terminationId) {
    final var termination = terminationEntityRepository.findByTerminationId(terminationId.id());
    return termination.map(TerminationEntityMapper::toDomain);
  }

  @Override
  public List<Termination> findAll() {
    final var all = terminationEntityRepository.findAll();
    return StreamSupport.stream(all.spliterator(), false)
        .map(TerminationEntityMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Termination> findByStatuses(List<TerminationStatus> statuses) {
    return terminationEntityRepository
        .findAllByStatusIsIn(
            statuses.stream().map(TerminationStatus::name).collect(Collectors.toList()))
        .stream()
        .map(TerminationEntityMapper::toDomain)
        .collect(Collectors.toList());
  }
}
