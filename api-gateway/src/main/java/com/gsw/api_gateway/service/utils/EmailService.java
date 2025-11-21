package com.gsw.api_gateway.service.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${mail.from:no-reply@example.com}")
    private String from;

    // JavaMailSender is optional; we do a runtime check before use
    private JavaMailSender mailSender;

    // Optional setter — Spring will call this if a JavaMailSender bean exists
    @Autowired(required = false)
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String toEmail, String name, String resetUrl) {
        String subject = "Password Reset";
        String html = "<p>Olá " + escapeHtml(name) + ",</p>" +
                "<p>Clique no link abaixo para resetar sua senha:</p>" +
                "<p><a href=\"" + escapeHtml(resetUrl) + "\">Resetar Senha</a></p>" +
                "<p>Se você não solicitou, por favor desconsidere este e-mail.</p>";

        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendEmailViaGmail(String toEmail, String subject, String htmlBody) {
        sendHtmlEmail(toEmail, subject, htmlBody);
    }


    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        if (mailSender == null) {
            throw new IllegalStateException("JavaMailSender bean not configured. Add 'spring-boot-starter-mail' and configure mail properties (spring.mail.*) or provide a JavaMailSender bean.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            System.out.println("Email sent to " + toEmail);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}