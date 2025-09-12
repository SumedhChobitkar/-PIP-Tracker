package com.pipTracker.Controller;

import com.pipTracker.Entity.Notification;
import com.pipTracker.Exception.NotificationNotFoundException;
import com.pipTracker.Service.Notificationservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/notification")
public class NotificationController
{
    @Autowired
    private Notificationservice notificationService;

    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
        }
        catch (NotificationNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not Found with ID"+userId);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id)
    {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("Notification marked as read.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        try {
            return ResponseEntity.ok(notificationService.createNotification(notification));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Saving Properly.");
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllNotifications(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(notificationService.getAllNotifications(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty ");
        }
    }
}
