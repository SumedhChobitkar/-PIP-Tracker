package com.pipTracker.Service;

public interface EmailService {
    void sendEmployeeRegistrationEmail(String toEmail, Long id, String name);
    void sendRegistrationSuccessEmail(String toEmail, String name);

}
