package se.inera.intyg.cts.testability.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto.CertificateTextDTO;
import se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto.CertificateXmlDTO;
import se.inera.intyg.cts.testability.dto.TestabilityExportEmbeddableDTO;
import se.inera.intyg.cts.testability.dto.TestabilityTerminationDTO;
import se.inera.intyg.cts.testability.service.TestabilityTerminationService;

@RestController
@Profile("testability")
@RequestMapping("/testability/v1/terminations")
public class TestabilityTerminationController {

  private static final Logger LOG = LoggerFactory.getLogger(TestabilityTerminationController.class);

  private final TestabilityTerminationService testabilityTerminationService;

  public TestabilityTerminationController(
      TestabilityTerminationService testabilityTerminationService) {
    this.testabilityTerminationService = testabilityTerminationService;
  }

  @PostMapping
  void create(@RequestBody TestabilityTerminationDTO testabilityTerminationDTO) {
    LOG.info("Create termination '{}'", testabilityTerminationDTO);
    testabilityTerminationService.createTermination(testabilityTerminationDTO);
  }

  @PostMapping("/{terminationId}/certificates")
  void saveCertificates(@PathVariable UUID terminationId,
      @RequestBody List<CertificateXmlDTO> certificateXmlDTOList) {
    LOG.info("Save '{}' certificates for termination '{}'", certificateXmlDTOList.size(),
        terminationId);
    testabilityTerminationService.saveCertificates(terminationId, certificateXmlDTOList);
  }

  @PostMapping("/{terminationId}/certificatetexts")
  void saveCertificateTexts(@PathVariable UUID terminationId,
      @RequestBody List<CertificateTextDTO> certificateTextDTOList) {
    LOG.info("Save '{}' certificate texts for termination '{}'", certificateTextDTOList.size(),
        terminationId);
    testabilityTerminationService.saveCertificateTexts(terminationId, certificateTextDTOList);
  }

  @PostMapping("/{terminationId}/upload")
  void setAsUploaded(@PathVariable UUID terminationId, @RequestBody String password) {
    LOG.info("Set termination '{}' as uploaded with password '{}'", terminationId, password);
    testabilityTerminationService.setAsUploaded(terminationId, password);
  }

  @PostMapping("/{terminationId}/sendNotification")
  void setAsNotificationSent(@PathVariable UUID terminationId,
      @RequestBody LocalDateTime notificationTime) {
    LOG.info("Set termination '{}' to notification sent at '{}'", terminationId, notificationTime);
    testabilityTerminationService.setAsNotificationSent(terminationId, notificationTime);
  }

  @PostMapping("/{terminationId}/receipt")
  void setAsReceiptRecieved(@PathVariable UUID terminationId) {
    LOG.info("Set status for termination '{}' to receipt received", terminationId);
    testabilityTerminationService.setAsReceiptReceived(terminationId);
  }

  @DeleteMapping("/{terminationId}")
  void delete(@PathVariable UUID terminationId) {
    LOG.info("Delete termination '{}'", terminationId);
    testabilityTerminationService.deleteTermination(terminationId);
  }

  @GetMapping("/export/{terminationId}")
  TestabilityExportEmbeddableDTO getExportEmbeddable(@PathVariable UUID terminationId) {
    return testabilityTerminationService.getExportEmbeddable(terminationId);
  }

  @GetMapping("/{terminationId}/certificatesCount")
  int getCertificatesCount(@PathVariable UUID terminationId) {
    LOG.info("Get certificates count for termination '{}'", terminationId);
    return testabilityTerminationService.getCertificatesCount(terminationId);
  }

  @GetMapping("/{terminationId}/certificateTextsCount")
  int getCertificateTextCount(@PathVariable UUID terminationId) {
    LOG.info("Get certificate texts count for termination '{}'", terminationId);
    return testabilityTerminationService.getCertificateTextsCount(terminationId);
  }

  @GetMapping("/{terminationId}/password")
  String getPassword(@PathVariable UUID terminationId) {
    LOG.info("Get package password for termination '{}'", terminationId);
    return testabilityTerminationService.getPassword(terminationId);
  }

  @GetMapping("/{terminationId}/status")
  String getStatus(@PathVariable UUID terminationId) {
    LOG.info("Get status for termination '{}'", terminationId);
    return testabilityTerminationService.getStatus(terminationId);
  }
}
