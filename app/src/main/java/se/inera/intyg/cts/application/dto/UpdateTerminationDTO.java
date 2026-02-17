package se.inera.intyg.cts.application.dto;

public record UpdateTerminationDTO(String hsaId,
                                   String personId,
                                   String phoneNumber,
                                   String emailAddress) {

}
