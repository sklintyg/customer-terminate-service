package se.inera.intyg.cts.infrastructure.persistence;

import static se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntityMapper.toEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntityMapper;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateTextEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@Repository
public class JpaCertificateTextRepository implements CertificateTextRepository {

  private final CertificateTextEntityRepository certificateTextEntityRepository;
  private final TerminationEntityRepository terminationEntityRepository;

  public JpaCertificateTextRepository(
      CertificateTextEntityRepository certificateTextEntityRepository,
      TerminationEntityRepository terminationEntityRepository) {
    this.certificateTextEntityRepository = certificateTextEntityRepository;
    this.terminationEntityRepository = terminationEntityRepository;
  }

  @Override
  public void store(Termination termination, List<CertificateText> certificateTexts) {
    final var terminationEntity = getByTerminationId(termination)
        .orElseThrow();

    certificateTextEntityRepository.saveAll(
        certificateTexts.stream()
            .map(certificateText -> toEntity(certificateText, terminationEntity))
            .collect(Collectors.toList())
    );
  }

  @Override
  public List<CertificateText> get(Termination termination) {
    final var terminationEntity = getByTerminationId(termination);

    if (terminationEntity.isEmpty()) {
      return Collections.emptyList();
    }

    return certificateTextEntityRepository.findAllByTermination(terminationEntity.get()).stream()
        .map(CertificateTextEntityMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void remove(Termination termination) {
    final var terminationEntity = getByTerminationId(termination)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Termination with id '%s' doesn't exists!",
                termination.terminationId().id())
        ));

    final var certificateTextEntities = certificateTextEntityRepository.findAllByTermination(
        terminationEntity);

    if (certificateTextEntities.size() > 0) {
      certificateTextEntityRepository.deleteAll(certificateTextEntities);
    }
  }

  private Optional<TerminationEntity> getByTerminationId(Termination termination) {
    return terminationEntityRepository.findByTerminationId(termination.terminationId().id());
  }
}
