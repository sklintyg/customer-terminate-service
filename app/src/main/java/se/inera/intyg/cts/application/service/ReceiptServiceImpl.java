package se.inera.intyg.cts.application.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  private static final Logger LOG = LoggerFactory.getLogger(ReceiptServiceImpl.class);

  private final TerminationRepository terminationRepository;

  public ReceiptServiceImpl(
      TerminationRepository terminationRepository) {
    this.terminationRepository = terminationRepository;
  }

  @Transactional
  public void handleReceipt(UUID terminationUUID) {
    LOG.info("Receipt received for termination id '{}'.", terminationUUID);
    final var receiptTime = LocalDateTime.now();
    final var terminationId = new TerminationId(terminationUUID);
    final var terminationOptional =
        terminationRepository.findByTerminationId(terminationId);

    final var termination =
        terminationOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format("Received receipt for non-existing terminationId '%s'.", terminationId))
        );

    termination.receiptReceived(receiptTime);
    terminationRepository.store(termination);
  }
}
