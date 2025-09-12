package com.pipTracker.Controller;

import com.pipTracker.Exception.EmailNotFoundException;
import com.pipTracker.Service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/mail")
public class EmailController {

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body)
    {
        try {
            emailSenderService.sendEmail(to, subject, body);
            return ResponseEntity.ok("Email sent successfully to: " + to);
        }
        catch (EmailNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter Values Properly");
        }
    }
}
