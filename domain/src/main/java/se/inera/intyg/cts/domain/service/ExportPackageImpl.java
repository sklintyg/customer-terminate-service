package se.inera.intyg.cts.domain.service;

import java.io.File;
import se.inera.intyg.cts.domain.model.Password;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class ExportPackageImpl implements ExportPackage {

  private final CreatePackage createPackage;
  private final UploadPackage uploadPackage;
  private final TerminationRepository terminationRepository;
  private final PasswordGenerator passwordGenerator;

  public ExportPackageImpl(CreatePackage createPackage,
      UploadPackage uploadPackage,
      TerminationRepository terminationRepository, PasswordGenerator passwordGenerator) {
    this.createPackage = createPackage;
    this.uploadPackage = uploadPackage;
    this.terminationRepository = terminationRepository;
    this.passwordGenerator = passwordGenerator;
  }

  @Override
  public void export(Termination termination) {
    final var password = new Password(passwordGenerator.generateSecurePassword());
    final var packageToExport = createPackage.create(termination, password);
    uploadPackage.uploadPackage(termination, packageToExport);
    removePackage(packageToExport);

    termination.exported(password);
    terminationRepository.store(termination);
  }

  private void removePackage(File packageToExport) {
    if (packageToExport.exists()) {
      packageToExport.delete();
    }
  }
}
