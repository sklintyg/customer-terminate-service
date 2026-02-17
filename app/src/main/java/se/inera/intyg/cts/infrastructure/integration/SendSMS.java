package se.inera.intyg.cts.infrastructure.integration;

import se.inera.intyg.cts.infrastructure.integration.tellustalk.dto.TellusTalkResponseDTO;

public interface SendSMS {

    TellusTalkResponseDTO sendSMS(String phonenumber, String message);

}