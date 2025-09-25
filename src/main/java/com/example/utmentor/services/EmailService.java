package com.example.utmentor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendOtpVerificationEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("UTMentor - Email Verification");
        
        String emailBody = String.format("""
            Dear Student,
            
            Welcome to UTMentor! Please verify your email address by entering the following OTP code:
            
            Verification Code: %s
            
            This code will expire in 10 minutes.
            
            If you did not request this verification, please ignore this email.
            
            Best regards,
            UTMentor Team
            """, otp);
        
        message.setText(emailBody);
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("UTMentor - Welcome!");
        
        String emailBody = String.format("""
            Dear %s,
            
            Congratulations! Your email has been successfully verified and your UTMentor account is now active.
            
            You can now log in to your account and start using all the features of UTMentor.
            
            Best regards,
            UTMentor Team
            """, firstName);
        
        message.setText(emailBody);
        mailSender.send(message);
    }
}
