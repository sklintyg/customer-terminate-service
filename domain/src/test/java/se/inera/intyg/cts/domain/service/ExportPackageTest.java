package se.inera.intyg.cts.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.defaultTermination;

import java.io.File;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.Password;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

@ExtendWith(MockitoExtension.class)
class ExportPackageTest {

  private static final String PASSWORD = "password";

  @Mock
  private PasswordGenerator passwordGenerator;
  @Mock
  private CreatePackage createPackageMock;
  @Mock
  private TerminationRepository terminationRepositoryMock;
  @Mock
  private UploadPackage uploadPackageMock;
  @InjectMocks
  private ExportPackageImpl exportPackage;
  @Mock
  private File packageFile;
  @Captor
  private ArgumentCaptor<Password> passwordCaptor;
  @Spy
  private Termination termination = defaultTermination();

  @BeforeEach
  void setUp() {
    when(passwordGenerator.generateSecurePassword()).thenReturn(PASSWORD);
    when(createPackageMock.create(any(Termination.class), any(Password.class))).thenReturn(
        packageFile);
  }

  @Test
  void testExport() {
    when(packageFile.exists()).thenReturn(true);

    exportPackage.export(termination);

    verify(createPackageMock, times(1)).create(any(Termination.class), passwordCaptor.capture());
    verify(uploadPackageMock, times(1)).uploadPackage(termination, packageFile);
    verify(packageFile, times(1)).delete();
    verify(termination, times(1)).exported(any(Password.class));
    verify(terminationRepositoryMock, times(1)).store(termination);
    assertEquals(passwordCaptor.getValue().password(), PASSWORD);
  }

  @Test
  void shallUpdateExportTimeAfterSuccessfulExport() {
    when(packageFile.exists()).thenReturn(true);

    final var beforeDateTime = LocalDateTime.now();
    exportPackage.export(termination);

    assertFalse(termination.export().exportTime().isBefore(beforeDateTime), () ->
        String.format("Expect exportTime '%s' to be updated and not before '%s'",
            termination.export().exportTime(), beforeDateTime)
    );
  }

  @Test
  void testExportFileDoNotExist() {
    when(packageFile.exists()).thenReturn(false);

    exportPackage.export(termination);

    verify(packageFile, never()).delete();
  }
}
