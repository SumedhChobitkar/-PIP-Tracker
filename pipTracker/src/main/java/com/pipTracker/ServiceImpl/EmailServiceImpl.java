package com.pipTracker.ServiceImpl;

import com.pipTracker.Service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmployeeRegistrationEmail(String toEmail, Long id, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Employee Added Successful");

            String content =
                    "Hello " + name + ",\n\n" +
                            "Your employee account has been created successfully.\n" +
                            "Your Employee ID: " + id + "\n" +
                            "Name: " + name + "\n\n" +
                            "Now you can move towards registration process."+
                            "Regards,\nAdmin Team";

            helper.setText(content);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendRegistrationSuccessEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Registration Successful");

            String content =
                    "Hello " + name + ",\n\n" +
                            "Your registration is completed successfully.\n" +
                            "You can now log in using your email.\n\n" +
                            "Regards,\nAdmin Team";

            helper.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send registration email: " + e.getMessage());
        }
    }



}
