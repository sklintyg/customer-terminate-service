package se.inera.intyg.cts.infrastructure.integration;

import jakarta.mail.MessagingException;

public interface SendEmail {

  void sendEmail(String emailAddress, String emailBody, String emailSubject)
      throws MessagingException;
}