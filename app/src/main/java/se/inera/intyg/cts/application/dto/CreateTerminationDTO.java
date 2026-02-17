package se.inera.intyg.cts.application.dto;

public record CreateTerminationDTO(String creatorHSAId,
                                   String creatorName,
                                   String hsaId,
                                   String organizationNumber,
                                   String personId,
                                   String phoneNumber,
                                   String emailAddress) {

}
