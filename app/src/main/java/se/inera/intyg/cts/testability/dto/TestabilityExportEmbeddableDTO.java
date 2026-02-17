package se.inera.intyg.cts.testability.dto;

import java.time.LocalDateTime;
import se.inera.intyg.cts.domain.model.Password;

public record TestabilityExportEmbeddableDTO(int total,
                                             int revoked,
                                             Password password,
                                             LocalDateTime receiptTime) {

}
