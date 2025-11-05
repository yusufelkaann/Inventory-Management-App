package com.yusufelkaan.jwt_auth.auth.utils;

import com.yusufelkaan.jwt_auth.auth.dtos.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {

    private final JavaMailSender mailSender;

    public EmailUtils(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(MailBody mailBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(mailBody.to());
        helper.setFrom("MY_MAIL");
        helper.setSubject(mailBody.subject());
        helper.setText(mailBody.text(), true); // set to true for HTML content

        mailSender.send(mimeMessage);
    }
}
