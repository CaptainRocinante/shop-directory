package com.rocinante.shops.service.sync;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
  private final SendGrid sendGrid;

  public EmailService(@Value("${sendgrid.api.key}") String sendgridApiKey) {
    this.sendGrid = new SendGrid(sendgridApiKey);
  }

  public void sendEmail(String fromName,
      String fromEmail,
      String subjectStr,
      String toStr,
      String contentStr) {
    final Email from = new Email(fromEmail, fromName);
    final Email to = new Email(toStr);
    final Content content = new Content("text/plain", contentStr);
    final Mail mail = new Mail(from, subjectStr, to, content);

    final Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      final Response response = sendGrid.api(request);
      if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
        log.info("Email sent successfully from {} to {} with subject {}",
            fromEmail, toStr, subjectStr);
      } else {
        log.error("Email send failed from {} to {} with subject {}",
            fromEmail, toStr, subjectStr);
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
