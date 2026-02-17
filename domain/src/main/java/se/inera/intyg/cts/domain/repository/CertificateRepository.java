package se.inera.intyg.cts.domain.repository;

import java.util.List;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.Termination;

public interface CertificateRepository {

  void store(Termination termination, List<Certificate> certificateList);

  List<Certificate> get(Termination termination);

  void remove(Termination termination);
}
