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
