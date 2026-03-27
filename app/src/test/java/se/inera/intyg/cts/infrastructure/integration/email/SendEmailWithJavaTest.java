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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SendEmailWithJavaTest {

  @Mock private JavaMailSender mailSender;

  @InjectMocks private SendEmailWithJava sendEmailWithJava;

  @Captor private ArgumentCaptor<MimeMessage> messageCaptor;

  private static final String EMAIL_SUBJECT = "Test email subject";
  private static final String EMAIL_CONTENT = "Test email content";
  private static final String EMAIL_TO_ADDRESS = "recipient@email.se";
  private static final String EMAIL_FROM_ADDRESS = "no-reply@test.se";
  private static final String EMAIL_CONTENT_TYPE = "text/html;charset=UTF-8";

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(sendEmailWithJava, "emailFromAddress", EMAIL_FROM_ADDRESS);
    doReturn(new MimeMessage(Session.getInstance(new Properties(), null)))
        .when(mailSender)
        .createMimeMessage();
  }

  @Test
  void shouldSetEmailToAddress() throws MessagingException {
    sendEmailWithJava.sendEmail(EMAIL_TO_ADDRESS, EMAIL_CONTENT, EMAIL_SUBJECT);

    verify(mailSender, times(1)).send(messageCaptor.capture());
    assertEquals(1, messageCaptor.getValue().getRecipients(RecipientType.TO).length);
    assertEquals(
        EMAIL_TO_ADDRESS, messageCaptor.getValue().getRecipients(RecipientType.TO)[0].toString());
  }

  @Test
  void shouldSetEmailFromAddress() throws MessagingException {
    sendEmailWithJava.sendEmail(EMAIL_TO_ADDRESS, EMAIL_CONTENT, EMAIL_SUBJECT);

    verify(mailSender, times(1)).send(messageCaptor.capture());
    assertEquals(1, messageCaptor.getValue().getFrom().length);
    assertEquals(EMAIL_FROM_ADDRESS, messageCaptor.getValue().getFrom()[0].toString());
  }

  @Test
  void shouldSetEmailSubject() throws MessagingException {
    sendEmailWithJava.sendEmail(EMAIL_TO_ADDRESS, EMAIL_CONTENT, EMAIL_SUBJECT);

    verify(mailSender, times(1)).send(messageCaptor.capture());
    assertEquals(EMAIL_SUBJECT, messageCaptor.getValue().getSubject());
  }

  @Test
  void shouldSetEmailContent() throws MessagingException, IOException {
    sendEmailWithJava.sendEmail(EMAIL_TO_ADDRESS, EMAIL_CONTENT, EMAIL_SUBJECT);

    verify(mailSender, times(1)).send(messageCaptor.capture());
    assertEquals(EMAIL_CONTENT, messageCaptor.getValue().getContent().toString());
    assertEquals(EMAIL_CONTENT_TYPE, messageCaptor.getValue().getContentType());
  }
}
