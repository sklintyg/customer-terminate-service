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
    final var terminationEntity = getByTerminationId(termination).orElseThrow();

    certificateTextEntityRepository.saveAll(
        certificateTexts.stream()
            .map(certificateText -> toEntity(certificateText, terminationEntity))
            .collect(Collectors.toList()));
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
    final var terminationEntity =
        getByTerminationId(termination)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format(
                            "Termination with id '%s' doesn't exists!",
                            termination.terminationId().id())));

    final var certificateTextEntities =
        certificateTextEntityRepository.findAllByTermination(terminationEntity);

    if (certificateTextEntities.size() > 0) {
      certificateTextEntityRepository.deleteAll(certificateTextEntities);
    }
  }

  private Optional<TerminationEntity> getByTerminationId(Termination termination) {
    return terminationEntityRepository.findByTerminationId(termination.terminationId().id());
  }
}
