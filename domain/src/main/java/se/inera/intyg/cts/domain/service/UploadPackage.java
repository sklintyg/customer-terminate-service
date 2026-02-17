package se.inera.intyg.cts.domain.service;

import java.io.File;
import se.inera.intyg.cts.domain.model.Termination;

public interface UploadPackage {

  void uploadPackage(Termination termination, File packageToUpload);
}
