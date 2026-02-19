package se.inera.intyg.cts.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.certificateEntities;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationBuilder;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateId;
import se.inera.intyg.cts.domain.model.CertificateXML;
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
  private Certificate certificate;

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
    List<Certificate> certificateList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Certificate certificate = mock(Certificate.class);
      CertificateId certificateId = mock(CertificateId.class);
      when(certificateId.id()).thenReturn(UUID.randomUUID().toString());
      when(certificate.certificateId()).thenReturn(certificateId);
      when(certificate.certificateXML()).thenReturn(new CertificateXML("<xml></xml>"));
      certificateList.add(certificate);
    }

    TerminationEntity te = terminationEntityRepository.save(terminationEntity);
    termination = defaultTerminationBuilder().terminationId(te.getTerminationId()).create();
    jpaCertificateRepository.store(termination, certificateList);

    assertEquals(3, certificateEntityRepository.count());
  }

  @Test
  void shallReturnCertificatesForExistingTermination() {
    TerminationEntity te = terminationEntityRepository.save(terminationEntity);

    TerminationEntity savedTerminationEntity =
        terminationEntityRepository.findByTerminationId(te.getTerminationId()).get();

    certificateEntityRepository.saveAll(certificateEntities(savedTerminationEntity, 3, 0));

    termination = defaultTerminationBuilder().terminationId(terminationEntity.getTerminationId())
        .create();
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

    termination = defaultTerminationBuilder().terminationId(terminationEntity.getTerminationId())
        .create();
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