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

  public ExportPackageImpl(
      CreatePackage createPackage,
      UploadPackage uploadPackage,
      TerminationRepository terminationRepository,
      PasswordGenerator passwordGenerator) {
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
