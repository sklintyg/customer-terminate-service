package se.inera.intyg.cts.domain.service;

import java.io.File;
import se.inera.intyg.cts.domain.model.Termination;

public class UploadPackageToMemory implements UploadPackage {

  private Termination termination;
  private File uploadedPackage;

  @Override
  public void uploadPackage(Termination termination, File packageToUpload) {
    this.termination = termination;
    this.uploadedPackage = packageToUpload;
  }

  public Termination termination() {
    return termination;
  }

  public File uploadedPackage() {
    return uploadedPackage;
  }
}
