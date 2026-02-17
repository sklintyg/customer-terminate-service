package se.inera.intyg.cts.domain.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;

public class InMemoryCertificateTextRepository implements CertificateTextRepository {

  private final Map<TerminationId, List<CertificateText>> certificateTextsMap = new HashMap<>();

  public int totalCount(TerminationId terminationId) {
    return certificateTextsMap.getOrDefault(terminationId, Collections.emptyList()).size();
  }

  @Override
  public void store(Termination termination, List<CertificateText> certificateTextsToStore) {
    final var certificateTexts = certificateTextsMap.getOrDefault(termination.terminationId(),
        new ArrayList<>());
    certificateTexts.addAll(certificateTextsToStore);
    certificateTextsMap.put(termination.terminationId(), certificateTexts);
  }

  @Override
  public List<CertificateText> get(Termination termination) {
    return certificateTextsMap.getOrDefault(termination.terminationId(), Collections.emptyList());
  }

  @Override
  public void remove(Termination termination) {
    certificateTextsMap.remove(termination.terminationId());
  }
}
