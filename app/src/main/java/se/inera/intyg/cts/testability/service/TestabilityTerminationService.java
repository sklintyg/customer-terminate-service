package se.inera.intyg.cts.testability.service;

import static se.inera.intyg.cts.testability.dto.TestabilityTerminationDTOMapper.toEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.CertificateId;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.CertificateType;
import se.inera.intyg.cts.domain.model.CertificateTypeVersion;
import se.inera.intyg.cts.domain.model.CertificateXML;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto.CertificateTextDTO;
import se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto.CertificateXmlDTO;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntityMapper;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntityMapper;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateTextEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;
import se.inera.intyg.cts.testability.dto.TestabilityExportEmbeddableDTO;
import se.inera.intyg.cts.testability.dto.TestabilityExportEmbeddableDTOMapper;
import se.inera.intyg.cts.testability.dto.TestabilityTerminationDTO;

@Service
public class TestabilityTerminationService {

  private final TerminationEntityRepository terminationEntityRepository;
  private final CertificateEntityRepository certificateEntityRepository;
  private final CertificateTextEntityRepository certificateTextEntityRepository;

  public TestabilityTerminationService(TerminationEntityRepository terminationEntityRepository,
      CertificateEntityRepository certificateEntityRepository,
      CertificateTextEntityRepository certificateTextEntityRepository) {
    this.terminationEntityRepository = terminationEntityRepository;
    this.certificateEntityRepository = certificateEntityRepository;
    this.certificateTextEntityRepository = certificateTextEntityRepository;
  }

  @Transactional
  public void createTermination(TestabilityTerminationDTO testabilityTerminationDTO) {
    terminationEntityRepository.save(toEntity(testabilityTerminationDTO));
  }

  @Transactional
  public void saveCertificates(UUID terminationId, List<CertificateXmlDTO> certificateXmlDTOList) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow();
    terminationEntity.setStatus(TerminationStatus.COLLECTING_CERTIFICATES_COMPLETED.name());
    terminationEntity.getExport().setTotal(certificateXmlDTOList.size());
    terminationEntity.getExport().setRevoked(
        (int) certificateXmlDTOList.stream()
            .filter(CertificateXmlDTO::revoked)
            .count());
    final var certificates = certificateXmlDTOList.stream()
        .map(certificateXmlDTO ->
            new Certificate(
                new CertificateId(certificateXmlDTO.id()),
                certificateXmlDTO.revoked(),
                new CertificateXML(certificateXmlDTO.xml())
            )
        )
        .map(certificate -> CertificateEntityMapper.toEntity(certificate, terminationEntity))
        .collect(Collectors.toList());

    certificateEntityRepository.saveAll(certificates);
    terminationEntityRepository.save(terminationEntity);
  }

  @Transactional
  public void saveCertificateTexts(UUID terminationId,
      List<CertificateTextDTO> certificateTextDTOList) {

    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow();
    terminationEntity.setStatus(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED.name());
    final var certificateTexts = certificateTextDTOList.stream()
        .map(certificateTextDTO ->
            new CertificateText(
                new CertificateType(certificateTextDTO.type()),
                new CertificateTypeVersion(certificateTextDTO.version()),
                new CertificateXML(certificateTextDTO.xml())
            )
        )
        .map(certificateText ->
            CertificateTextEntityMapper.toEntity(certificateText, terminationEntity)
        )
        .collect(Collectors.toList());

    certificateTextEntityRepository.saveAll(certificateTexts);
    terminationEntityRepository.save(terminationEntity);
  }

  @Transactional
  public void deleteTermination(UUID terminationId) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId);
    terminationEntity.ifPresent(this::deleteTermination);
  }

  public TestabilityExportEmbeddableDTO getExportEmbeddable(UUID terminationId) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId);
    return TestabilityExportEmbeddableDTOMapper.toDomain(
        terminationEntity.orElseThrow().getExport());
  }

  public int getCertificatesCount(UUID terminationId) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId);
    return terminationEntity.orElseThrow().getExport().getTotal();
  }

  public int getCertificateTextsCount(UUID terminationId) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId);
    return certificateTextEntityRepository.findAllByTermination(terminationEntity.orElseThrow())
        .size();
  }

  private void deleteTermination(TerminationEntity terminationEntity) {
    final var certificateEntityList = certificateEntityRepository.findAllByTermination(
        terminationEntity);
    certificateEntityRepository.deleteAll(certificateEntityList);

    final var certificateTextEntityList = certificateTextEntityRepository.findAllByTermination(
        terminationEntity);
    certificateTextEntityRepository.deleteAll(certificateTextEntityList);

    terminationEntityRepository.delete(terminationEntity);
  }

  public String getPassword(UUID terminationId) {
    return terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow()
        .getExport()
        .getPassword();
  }

  @Transactional
  public void setAsUploaded(UUID terminationId, String password) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow();

    terminationEntity.setStatus(TerminationStatus.EXPORTED.name());
    terminationEntity.getExport().setPassword(password);

    terminationEntityRepository.save(terminationEntity);
  }

  @Transactional
  public void setAsNotificationSent(UUID terminationId, LocalDateTime notificationTime) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow();

    terminationEntity.setStatus(TerminationStatus.NOTIFICATION_SENT.name());
    terminationEntity.getExport().setNotificationTime(notificationTime);

    terminationEntityRepository.save(terminationEntity);
  }

  @Transactional
  public void setAsReceiptReceived(UUID terminationId) {
    final var terminationEntity = terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow();

    terminationEntity.setStatus(TerminationStatus.RECEIPT_RECEIVED.name());
    terminationEntity.getExport().setReceiptTime(LocalDateTime.now());

    terminationEntityRepository.save(terminationEntity);
  }

  public String getStatus(UUID terminationId) {
    return terminationEntityRepository.findByTerminationId(terminationId)
        .orElseThrow()
        .getStatus();
  }
}
