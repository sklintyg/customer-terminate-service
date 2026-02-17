package se.inera.intyg.cts.testability.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestabilityTerminationDTO(UUID terminationId,
                                        LocalDateTime created,
                                        String creatorHSAId,
                                        String creatorName,
                                        String status,
                                        String hsaId,
                                        String organizationNumber,
                                        String personId,
                                        String phoneNumber,
                                        String emailAddress) {

}
