package se.inera.intyg.cts.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;

@Repository
public interface TerminationEntityRepository extends CrudRepository<TerminationEntity, Long> {

  Optional<TerminationEntity> findByTerminationId(UUID terminationId);

  List<TerminationEntity> findAllByStatusIsIn(List<String> statuses);
}
