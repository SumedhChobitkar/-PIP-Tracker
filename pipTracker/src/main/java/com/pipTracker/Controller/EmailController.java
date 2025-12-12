package com.pipTracker.Controller;

import com.pipTracker.Exception.EmailNotFoundException;
import com.pipTracker.Service.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Email APIs",
        description = "Operations related to sending emails"
)
@RestController
@CrossOrigin("*")
@RequestMapping("/api/mail")
public class EmailController {

    @Autowired
    private EmailSenderService emailSenderService;

     @Operation(
            summary = "Send Email",
            description = "Sends an email by providing recipient address, subject, and body.\n\n" +
                    "Eg: POST http://localhost:8080/api/mail/send?to=test@gmail.com&subject=Hello&body=Welcome"
    )
    @ApiResponse(responseCode = "200", description = "Email sent successfully")
    @ApiResponse(responseCode = "400", description = "Invalid email or missing parameters")
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {
        try {
            emailSenderService.sendEmail(to, subject, body);
            return ResponseEntity.ok("Email sent successfully to: " + to);
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter Values Properly");
        }
    }
}
