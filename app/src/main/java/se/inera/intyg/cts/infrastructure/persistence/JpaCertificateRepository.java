package se.inera.intyg.cts.infrastructure.persistence;

import static se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntityMapper.toEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntityMapper;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@Repository
public class JpaCertificateRepository implements CertificateRepository {

  private final CertificateEntityRepository certificateEntityRepository;
  private final TerminationEntityRepository terminationEntityRepository;

  public JpaCertificateRepository(CertificateEntityRepository certificateEntityRepository,
      TerminationEntityRepository terminationEntityRepository) {
    this.certificateEntityRepository = certificateEntityRepository;
    this.terminationEntityRepository = terminationEntityRepository;
  }

  @Override
  public void store(Termination termination, List<Certificate> certificateList) {
    final var terminationEntity = getTerminationEntity(termination);

    certificateEntityRepository.saveAll(
        certificateList.stream()
            .map(certificate -> toEntity(certificate, terminationEntity))
            .collect(Collectors.toList())
    );
  }

  @Override
  public List<Certificate> get(Termination termination) {
    final var terminationEntity = terminationEntityRepository
        .findByTerminationId(termination.terminationId().id());

    if (terminationEntity.isEmpty()) {
      return Collections.emptyList();
    }

    return certificateEntityRepository.findAllByTermination(terminationEntity.get()).stream()
        .map(CertificateEntityMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void remove(Termination termination) {
    final var terminationEntity = terminationEntityRepository
        .findByTerminationId(termination.terminationId().id())
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Termination with id '%s' doesn't exists!",
                termination.terminationId().id())
        ));

    final var certificateEntities = certificateEntityRepository.findAllByTermination(
        terminationEntity);

    if (certificateEntities.size() > 0) {
      certificateEntityRepository.deleteAll(certificateEntities);
    }
  }

  private TerminationEntity getTerminationEntity(Termination termination) {
    return terminationEntityRepository
        .findByTerminationId(termination.terminationId().id())
        .orElseThrow(() ->
            new IllegalArgumentException(
                String.format("Termination with id '%s' doesn't exists!",
                    termination.terminationId().id())
            )
        );
  }
}
