package se.inera.intyg.cts.domain.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;

public class InMemoryCertificateRepository implements CertificateRepository {

  private final Map<TerminationId, List<Certificate>> certificatesMap = new HashMap<>();

  public int totalCount(TerminationId terminationId) {
    return certificatesMap.getOrDefault(terminationId, Collections.emptyList()).size();
  }

  @Override
  public void store(Termination termination, List<Certificate> certificatesToStore) {
    final var certificates = certificatesMap.getOrDefault(termination.terminationId(),
        new ArrayList<>());
    certificates.addAll(certificatesToStore);
    certificatesMap.put(termination.terminationId(), certificates);
  }

  @Override
  public List<Certificate> get(Termination termination) {
    return certificatesMap.getOrDefault(termination.terminationId(), Collections.emptyList());
  }

  @Override
  public void remove(Termination termination) {
    certificatesMap.remove(termination.terminationId());
  }
}
