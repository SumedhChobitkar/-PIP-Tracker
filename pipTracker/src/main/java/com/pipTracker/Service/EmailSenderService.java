package com.pipTracker.Service;

public interface EmailSenderService {
     void sendEmail(String toEmail, String subject, String body);
}
