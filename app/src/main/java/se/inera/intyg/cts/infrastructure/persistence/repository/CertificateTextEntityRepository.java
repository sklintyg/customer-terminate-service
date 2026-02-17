package se.inera.intyg.cts.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;

@Repository
public interface CertificateTextEntityRepository extends
    CrudRepository<CertificateTextEntity, Long> {

  List<CertificateTextEntity> findAllByTermination(TerminationEntity terminationEntity);
}
