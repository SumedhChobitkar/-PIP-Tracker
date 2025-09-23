package com.pipTracker.Controller;

import com.pipTracker.Entity.Notification;
import com.pipTracker.Exception.NotificationNotFoundException;
import com.pipTracker.Service.Notificationservice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Notification APIs",
        description = "APIs for managing user notifications"
)
@RestController
@CrossOrigin("*")
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private Notificationservice notificationService;

    @Operation(
            summary = "Get Unread Notifications",
            description = "Fetches all unread notifications for a specific user by their ID.\n\n" +
                    "Eg: GET http://localhost:8080/api/notification/unread/{userId}"
    )
    @ApiResponse(responseCode = "200", description = "Unread notifications fetched successfully")
    @ApiResponse(responseCode = "404", description = "No unread notifications found")
    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
        } catch (NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found with ID " + userId);
        }
    }

    @Operation(
            summary = "Mark Notification as Read",
            description = "Marks a notification as read by its ID.\n\n" +
                    "Eg: PUT http://localhost:8080/api/notification/{id}/read"
    )
    @ApiResponse(responseCode = "200", description = "Notification marked as read successfully")
    @ApiResponse(responseCode = "400", description = "Invalid notification ID")
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("Notification marked as read.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");
        }
    }

    @Operation(
            summary = "Create Notification",
            description = "Creates a new notification.\n\n" +
                    "Eg: POST http://localhost:8080/api/notification"
    )
    @ApiResponse(responseCode = "200", description = "Notification created successfully")
    @ApiResponse(responseCode = "400", description = "Error while creating notification")
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        try {
            return ResponseEntity.ok(notificationService.createNotification(notification));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not saving properly.");
        }
    }

    @Operation(
            summary = "Get All Notifications",
            description = "Fetches all notifications (read + unread) for a specific user.\n\n" +
                    "Eg: GET http://localhost:8080/api/notification/all/{userId}"
    )
    @ApiResponse(responseCode = "200", description = "All notifications fetched successfully")
    @ApiResponse(responseCode = "404", description = "No notifications found")
    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllNotifications(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(notificationService.getAllNotifications(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty");
        }
    }
}
