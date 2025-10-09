


package com.pipTracker.ServiceImpl;


import com.pipTracker.Entity.Notification;
import com.pipTracker.Exception.NotificationNotFoundException;
import com.pipTracker.Repository.NotificationRepository;
import com.pipTracker.ServiceImpl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        notification = new Notification();
        notification.setUserId(100L);
        notification.setTitle("Task Update");
        notification.setMessage("You have a new task");
        notification.setType("INFO");
        notification.setIsRead(false);
    }

    @Test

    void testCreateNotification_Success() {
        when(notificationRepository.save(notification)).thenReturn(notification);

        Notification result = notificationService.createNotification(notification);

        assertNotNull(result);
        assertEquals("Task Update", result.getTitle());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testCreateNotification_InvalidTitle_ThrowsException() {
        notification.setTitle(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificationService.createNotification(notification));

        assertEquals("Input Valid Title", ex.getMessage());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetUnreadNotifications_Success() {
        when(notificationRepository.findByUserIdAndIsReadFalse(100L))
                .thenReturn(Arrays.asList(notification));

        List<Notification> result = notificationService.getUnreadNotifications(100L);

        assertEquals(1, result.size());
        verify(notificationRepository, times(1))
                .findByUserIdAndIsReadFalse(100L);
    }

    @Test
    void testGetUnreadNotifications_NotFound() {
        when(notificationRepository.findByUserIdAndIsReadFalse(100L))
                .thenReturn(Collections.emptyList());

        assertThrows(NotificationNotFoundException.class,
                () -> notificationService.getUnreadNotifications(100L));
    }

    @Test
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        notificationService.markAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testMarkAsRead_NotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> notificationService.markAsRead(99L));

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetAllNotifications_Success() {
        when(notificationRepository.findByUserId(100L))
                .thenReturn(Arrays.asList(notification));

        List<Notification> result = notificationService.getAllNotifications(100L);

        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByUserId(100L);
    }

    @Test
    void testGetAllNotifications_NotFound() {
        when(notificationRepository.findByUserId(100L))
                .thenReturn(Collections.emptyList());

        assertThrows(NotificationNotFoundException.class,
                () -> notificationService.getAllNotifications(100L));
    }

    @Test
    void testUpdatePip_Success() {
        when(notificationRepository.save(notification)).thenReturn(notification);

        Notification result = notificationService.updatePip(notification);

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(notification);
    }
}
