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
    final var termination = terminationEntityRepository.findByTerminationId(
        terminationId.id());
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
    return terminationEntityRepository.findAllByStatusIsIn(
            statuses.stream()
                .map(TerminationStatus::name)
                .collect(Collectors.toList())
        ).stream()
        .map(TerminationEntityMapper::toDomain)
        .collect(Collectors.toList());
  }
}
