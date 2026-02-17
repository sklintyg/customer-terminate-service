package se.inera.intyg.cts.infrastructure.integration.tellustalk.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SMSRequestDTOTest {

    private final String OkPhoneNumber = "sms:+468102121212";
    private final String BadPhoneNumber = "sms:+4080812121212";
    private final String OkPassword = "XD12!!aabb";
    private final String BadPassword = "";
    private final String OkSMSOriginator = "Inera AB";
    private final String BadSMSOriginator = "För långt företagsnamn AB";

    @Test
    void SMSRequestDTOOk(){
        new SMSRequestDTO(OkPhoneNumber, OkPassword, OkSMSOriginator);
    }

    @Test
    void SMSRequestDTOBadPhoneNumber(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SMSRequestDTO(BadPhoneNumber, OkPassword, OkSMSOriginator));
        assertEquals("SMS Phone number 'sms:+4080812121212' format must match 'sms:+46704000000'.", exception.getMessage());
    }

    @Test
    void SMSRequestDTOBadPassword(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SMSRequestDTO(OkPhoneNumber, BadPassword, OkSMSOriginator));
        assertEquals("Empty password SMS message is not allowed.", exception.getMessage());
    }

    @Test
    void SMSRequestDTOBadSMSOriginator(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SMSRequestDTO(OkPhoneNumber, OkPassword, BadSMSOriginator));
        assertEquals("SMS originator text is longer than 11 characters.", exception.getMessage());
    }
}