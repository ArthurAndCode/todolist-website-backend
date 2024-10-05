package com.arthur.code.todolist_website_backend.service.userService;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    private static final String PASSWORD_RESET_SUBJECT = "Reset Your Password";
    private static final String TEMPORARY_PASSWORD_SUBJECT = "Your New Password";
    private static final String RESET_PASSWORD_MESSAGE_TEMPLATE = """
            Dear %s,

            We received a request to reset your password. To proceed with resetting your password, please click on the link below:

            %s

            If you did not request this password reset, please ignore this email.

            Best regards,
            To Do List - MakeItSmooth Support
            """;
    private static final String TEMPORARY_PASSWORD_MESSAGE_TEMPLATE = """
            Dear %s,

            Your password has been successfully reset. Below is your new temporary password, which you can use to log in:

            Temporary Password: %s

            We recommend changing this temporary password to something more secure as soon as possible.

            Best regards,
            To Do List - MakeItSmooth Support
            """;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendPasswordResetLink(String email, String token) {
        String resetLink = "http://localhost:9090/api/v1/user/reset-password?token=" + token;
        String message = String.format(RESET_PASSWORD_MESSAGE_TEMPLATE, email, resetLink);
        sendEmail(email, PASSWORD_RESET_SUBJECT, message);
    }

    public void sendTemporaryPassword(String email, String temporaryPassword) {
        String message = String.format(TEMPORARY_PASSWORD_MESSAGE_TEMPLATE, email, temporaryPassword);
        sendEmail(email, TEMPORARY_PASSWORD_SUBJECT, message);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage mailMessage = buildMailMessage(to, subject, text);
        try {
            emailSender.send(mailMessage);
        } catch (MailException e) {
            throw new IllegalStateException("Failed to send email.");
        }
    }

    private SimpleMailMessage buildMailMessage(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        return mailMessage;
    }
}


