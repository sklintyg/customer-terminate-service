package se.inera.intyg.cts.domain.repository;

import java.util.List;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.Termination;

public interface CertificateTextRepository {

  void store(Termination termination, List<CertificateText> certificateTexts);

  List<CertificateText> get(Termination termination);

  void remove(Termination termination);
}
