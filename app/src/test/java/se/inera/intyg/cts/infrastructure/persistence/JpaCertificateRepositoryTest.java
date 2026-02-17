package se.inera.intyg.cts.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.certificateEntities;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.certificates;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@DataJpaTest
class JpaCertificateRepositoryTest {

  @Autowired
  private TerminationEntityRepository terminationEntityRepository;
  @Autowired
  private CertificateEntityRepository certificateEntityRepository;
  private JpaCertificateRepository jpaCertificateRepository;
  private Termination termination;
  private TerminationEntity terminationEntity;

  @BeforeEach
  void setUp() {
    jpaCertificateRepository = new JpaCertificateRepository(
        certificateEntityRepository,
        terminationEntityRepository);
    termination = defaultTermination();
    terminationEntity = defaultTerminationEntity();
  }

  @Test
  void shallStoreCertificatesForExistingTermination() {
    terminationEntityRepository.save(terminationEntity);
    jpaCertificateRepository.store(termination, certificates(3, 0));

    assertEquals(3, certificateEntityRepository.count());
  }

  @Test
  void shallReturnCertificatesForExistingTermination() {
    terminationEntityRepository.save(terminationEntity);

    TerminationEntity savedTerminationEntity =
        terminationEntityRepository.findByTerminationId(terminationEntity.getTerminationId()).get();

    certificateEntityRepository.saveAll(certificateEntities(savedTerminationEntity, 3, 0));

    assertEquals(3, jpaCertificateRepository.get(termination).size());
  }

  @Test
  void shallReturnEmptyForForMissingTermination() {
    assertEquals(0, jpaCertificateRepository.get(termination).size());
  }

  @Test
  void shallRemoveCertificatesForExistingTermination() {
    terminationEntityRepository.save(terminationEntity);
    TerminationEntity savedTerminationEntity =
        terminationEntityRepository.findByTerminationId(terminationEntity.getTerminationId()).get();

    certificateEntityRepository.saveAll(certificateEntities(savedTerminationEntity, 3, 0));

    jpaCertificateRepository.remove(termination);

    assertEquals(0, jpaCertificateRepository.get(termination).size());
  }

  @Test
  void shallThrowExceptionForMissingTermination() {
    assertThrows(IllegalArgumentException.class,
        () -> jpaCertificateRepository.remove(termination)
    );
  }
}