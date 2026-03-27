/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.infrastructure.integration.email;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_INFO;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import se.inera.intyg.cts.infrastructure.integration.SendEmail;
import se.inera.intyg.cts.logging.PerformanceLogging;

@Service
public class SendEmailWithJava implements SendEmail {

  @Value("${message.notification.email.from.address}")
  private String emailFromAddress;

  private final JavaMailSender mailSender;

  public SendEmailWithJava(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  @PerformanceLogging(eventAction = "send-email", eventType = EVENT_TYPE_INFO)
  public void sendEmail(String emailAddress, String emailBody, String emailSubject)
      throws MessagingException {
    final var message = createMessage(emailAddress, emailSubject, emailBody);
    message.saveChanges();
    mailSender.send(message);
  }

  private MimeMessage createMessage(String emailAddress, String emailSubject, String emailBody)
      throws MessagingException {
    final var mimeMessage = mailSender.createMimeMessage();
    final var mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    mimeMessage.setFrom(new InternetAddress(emailFromAddress));
    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
    mimeMessageHelper.setSubject(emailSubject);
    mimeMessageHelper.setText(emailBody, true);
    return mimeMessage;
  }
}
