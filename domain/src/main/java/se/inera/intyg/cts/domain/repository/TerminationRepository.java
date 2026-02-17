package se.inera.intyg.cts.domain.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.model.TerminationStatus;

public interface TerminationRepository {

  Termination store(Termination termination);

  Optional<Termination> findByTerminationId(TerminationId id);

  List<Termination> findAll();

  List<Termination> findByStatuses(List<TerminationStatus> statuses);
}
