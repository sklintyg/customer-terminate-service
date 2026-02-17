package se.inera.intyg.cts.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.infrastructure.service.CreateEncryptedZipPackage.REVOKED_FILENAME;
import static se.inera.intyg.cts.infrastructure.service.CreateEncryptedZipPackage.XML_EXTENSION;
import static se.inera.intyg.cts.testutil.CertificateTestDataBuilder.certificateEntities;
import static se.inera.intyg.cts.testutil.CertificateTextTestDataBuilder.certificateTextEntities;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.AbstractFileHeader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.Password;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.CertificateTextEntity;
import se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntity;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.CertificateTextEntityRepository;
import se.inera.intyg.cts.infrastructure.persistence.repository.TerminationEntityRepository;

@ExtendWith(MockitoExtension.class)
class CreateEncryptedZipPackageTest {

  public static final String DESTINATION_DIRECTORY = "./destination_directory";
  @Mock
  private TerminationEntityRepository terminationEntityRepository;
  @Mock
  private CertificateEntityRepository certificateEntityRepository;
  @Mock
  private CertificateTextEntityRepository certificateTextEntityRepository;
  private CreateEncryptedZipPackage createEncryptedZipPackage;

  private Termination termination;
  private TerminationEntity terminationEntity;
  private List<CertificateEntity> certificates;
  private List<CertificateTextEntity> certificateTexts;

  private final Password password = new Password("thisismypassword");
  private File zipFile;

  @BeforeEach
  void setUp() {

    createEncryptedZipPackage = new CreateEncryptedZipPackage(
        terminationEntityRepository,
        certificateEntityRepository,
        certificateTextEntityRepository,
        "./"
    );

    termination = defaultTermination();
    terminationEntity = defaultTerminationEntity();
    when(terminationEntityRepository.findByTerminationId(termination.terminationId().id())).thenReturn(Optional.of(terminationEntity));

    certificates = certificateEntities(terminationEntity, 20, 5);
    when(certificateEntityRepository.findAllByTermination(terminationEntity)).thenReturn(certificates);

    certificateTexts = certificateTextEntities(terminationEntity, 10);
    when(certificateTextEntityRepository.findAllByTermination(terminationEntity)).thenReturn(certificateTexts);
  }

  @AfterEach
  void tearDown() {
    if (zipFile != null) {
      try {
        Files.deleteIfExists(Path.of(zipFile.getPath()));
        if (Files.exists(Path.of(DESTINATION_DIRECTORY))) {
          Files.walk(Path.of(DESTINATION_DIRECTORY))
              .sorted(Comparator.reverseOrder())
              .map(Path::toFile)
              .forEach(File::delete);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  void shallReturnZipFileNamedWithHsaId() {
    zipFile = createEncryptedZipPackage.create(termination, password);
    assertEquals(DEFAULT_HSA_ID + ".zip", zipFile.getName());
  }

  @Test
  void shallRemoveWorkingFolderAfterCreatingZipFile() {
    zipFile = createEncryptedZipPackage.create(termination, password);
    assertFalse(Files.isDirectory(Path.of("./" + DEFAULT_HSA_ID)));
  }

  @Test
  void shallReturnZipFileThatIsEncrypted() throws ZipException {
    zipFile = createEncryptedZipPackage.create(termination, password);
    assertTrue(new ZipFile(zipFile.getPath()).isEncrypted());
  }

  @Test
  void shallReturnZipFileThatCannotBeUnzippedWithoutPassword() {
    zipFile = createEncryptedZipPackage.create(termination, password);
    assertThrows(ZipException.class,
        () -> new ZipFile(zipFile.getPath())
            .extractAll(DESTINATION_DIRECTORY)
    );
  }

  @Test
  void shallReturnZipFileThatCanBeUnzippedWithCorrectPassword() throws ZipException {
    zipFile = createEncryptedZipPackage.create(termination, password);
    new ZipFile(zipFile.getPath(), password.password().toCharArray())
        .extractAll(DESTINATION_DIRECTORY);
  }

  @Test
  void shallReturnZipFileThatCannotBeUnzippedWithIncorrectPassword() {
    zipFile = createEncryptedZipPackage.create(termination, password);
    assertThrows(ZipException.class,
        () -> new ZipFile(zipFile.getPath(), "incorrectPassword".toCharArray())
            .extractAll(DESTINATION_DIRECTORY)
    );
  }

  @Test
  void shallReturnZipFileThatIncludesCertificateDirectory() throws ZipException {
    zipFile = createEncryptedZipPackage.create(termination, password);
    final var fileHeaders = new ZipFile(zipFile.getPath(),
        password.password().toCharArray()).getFileHeaders().stream()
        .map(AbstractFileHeader::getFileName)
        .toList();

    final var certificatesDirectory = termination.careProvider().hsaId().id() + "/intyg/";
    assertTrue(fileHeaders.contains(certificatesDirectory),
        () -> String.format("Missing directory %s in zip file containing %s",
            certificatesDirectory, fileHeaders));
  }

  @Test
  void shallReturnZipFileThatIncludesCertificateTextDirectory() throws ZipException {
    zipFile = createEncryptedZipPackage.create(termination, password);
    final var fileHeaders = new ZipFile(zipFile.getPath(),
        password.password().toCharArray()).getFileHeaders().stream()
        .map(AbstractFileHeader::getFileName)
        .toList();

    final var certificatesDirectory = termination.careProvider().hsaId().id() + "/texter/";
    assertTrue(fileHeaders.contains(certificatesDirectory),
        () -> String.format("Missing directory %s in zip file containing %s",
            certificatesDirectory, fileHeaders));
  }

  @Test
  void shallReturnZipFileThatIncludesCertificates() throws ZipException {
    zipFile = createEncryptedZipPackage.create(termination, password);
    final var fileHeaders = new ZipFile(zipFile.getPath(),
        password.password().toCharArray()).getFileHeaders().stream()
        .map(AbstractFileHeader::getFileName)
        .toList();

    for (CertificateEntity certificateEntity : certificates) {
      assertTrue(
          fileHeaders.stream().anyMatch(s -> s.contains(certificateFileName(certificateEntity))),
          () -> String.format("Certificate with filename %s is missing in zip file",
              certificateFileName(certificateEntity)));
    }
  }

  @Test
  void shallReturnZipFileThatIncludesCertificateTexts() throws ZipException {
    zipFile = createEncryptedZipPackage.create(termination, password);
    final var fileHeaders = new ZipFile(zipFile.getPath(),
        password.password().toCharArray()).getFileHeaders().stream()
        .map(AbstractFileHeader::getFileName)
        .toList();

    for (CertificateTextEntity certificateEntity : certificateTexts) {
      assertTrue(
          fileHeaders.stream()
              .anyMatch(s -> s.contains(certificateTextFileName(certificateEntity))),
          () -> String.format("CertificateText with filename %s is missing in zip file",
              certificateTextFileName(certificateEntity)));
    }
  }

  private String certificateFileName(CertificateEntity certificate) {
    return certificate.getCertificateId() + revokedFileName(certificate) + XML_EXTENSION;
  }

  private String revokedFileName(CertificateEntity certificate) {
    return certificate.isRevoked() ? REVOKED_FILENAME : "";
  }

  private String certificateTextFileName(CertificateTextEntity certificateTextEntity) {
    return certificateTextEntity.getCertificateType() + "-"
        + certificateTextEntity.getCertificateTypeVersion() + XML_EXTENSION;
  }
}