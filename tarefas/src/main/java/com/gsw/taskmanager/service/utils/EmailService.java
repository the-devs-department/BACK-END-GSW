package com.gsw.taskmanager.service.utils;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.mailersend.sdk.templates.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${mailersend.api.key}")
    private String TOKEN;

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String name, String resetUrl) {
        try {
            // Initialize MailerSend client
            MailerSend mailerSend = new MailerSend();
            mailerSend.setToken(TOKEN);

            // Build the email
            Email email = new Email();
            email.setFrom("GSW Support", "no-reply@test-nrw7gymxkrjg2k8e.mlsender.net");
            email.addRecipient(name, toEmail);
            email.setSubject("Password Reset");

            // Simple HTML body with the reset link
            email.setHtml(
                    "<p>Olá " + name + ",</p>" +
                            "<p>Clique no link abaixo para resetar sua senha:</p>" +
                            "<a href='" + resetUrl + "'>Resetar Senha</a>" +
                            "<p>Se você não solicitou, por favor desconsiderar esse e-mail.</p>"
            );

            // Send
            MailerSendResponse response = mailerSend.emails().send(email);
            System.out.println("✅ Email sent! Message ID: " + response.messageId);

        } catch (MailerSendException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to send email: " + e.getMessage());
        }
    }

    public void sendEmailViaGmail(String toEmail, String subject,String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            message.setFrom("gswnoreplyplease@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Email sent!");

    }
}