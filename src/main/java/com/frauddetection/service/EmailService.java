package com.frauddetection.service;

// Lombok annotations removed
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender emailSender;
    
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    // OTP email method removed as per requirement
    
    public void sendFraudAlertEmail(String to, String alertDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Fraud Detection System - Suspicious Activity Alert");
        message.setText("We have detected suspicious activity on your account:\n\n" + alertDetails + 
                "\n\nIf this was not you, please contact our support team immediately to secure your account.");
        
        emailSender.send(message);
    }
    
    public void sendAccountLockedEmail(String to, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Fraud Detection System - Account Locked");
        message.setText("Your account has been temporarily locked due to: " + reason + 
                "\n\nYour account will be automatically unlocked after 1 hour. If you need immediate assistance, " +
                "please contact our support team.");
        
        emailSender.send(message);
    }
}
