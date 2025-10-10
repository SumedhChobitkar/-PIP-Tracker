

package com.pipTracker.Controller;


import com.pipTracker.Controller.NotificationController;
import com.pipTracker.Entity.Notification;
import com.pipTracker.Exception.NotificationNotFoundException;
import com.pipTracker.Service.Notificationservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private Notificationservice notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setUserId(100L);
        notification.setTitle("Test Title");
        notification.setMessage("This is a test message");
        notification.setType("INFO");
        notification.setIsRead(false);
    }

    @Test
    void testGetUnreadNotifications_Success() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getUnreadNotifications(100L)).thenReturn(notifications);

        ResponseEntity<?> response = notificationController.getUnreadNotifications(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
        verify(notificationService, times(1)).getUnreadNotifications(100L);
    }

    @Test
    void testGetUnreadNotifications_NotFound() {
        when(notificationService.getUnreadNotifications(100L))
                .thenThrow(new NotificationNotFoundException("Notification not found"));

        ResponseEntity<?> response = notificationController.getUnreadNotifications(100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Notification not found with ID 100", response.getBody());
    }

    @Test
    void testMarkAsRead_Success() {
        ResponseEntity<?> response = notificationController.markAsRead(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notification marked as read.", response.getBody());
        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    void testMarkAsRead_BadRequest() {
        doThrow(new RuntimeException("Id not found")).when(notificationService).markAsRead(99L);

        ResponseEntity<?> response = notificationController.markAsRead(99L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Id not found", response.getBody());
    }

    @Test
    void testCreateNotification_Success() {
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        ResponseEntity<?> response = notificationController.createNotification(notification);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notification, response.getBody());
        verify(notificationService, times(1)).createNotification(notification);
    }

    @Test
    void testCreateNotification_ValidationError() {
        when(notificationService.createNotification(any(Notification.class)))
                .thenThrow(new IllegalArgumentException("Invalid title"));

        ResponseEntity<?> response = notificationController.createNotification(notification);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Validations Issues.Invalid title", response.getBody());
    }

    @Test
    void testCreateNotification_BadRequest() {
        when(notificationService.createNotification(any(Notification.class)))
                .thenThrow(new RuntimeException("Not saving properly"));

        ResponseEntity<?> response = notificationController.createNotification(notification);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not saving properly.", response.getBody());
    }

    @Test
    void testGetAllNotifications_Success() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getAllNotifications(100L)).thenReturn(notifications);

        ResponseEntity<?> response = notificationController.getAllNotifications(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
    }

    @Test
    void testGetAllNotifications_NotFound() {
        when(notificationService.getAllNotifications(100L))
                .thenThrow(new RuntimeException("Empty"));

        ResponseEntity<?> response = notificationController.getAllNotifications(100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Empty", response.getBody());
    }
}
