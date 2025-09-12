package com.pipTracker.Service;

import com.pipTracker.Entity.Notification;
import java.util.List;

public interface Notificationservice {
    Notification createNotification(Notification notification);
    List<Notification> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
    List<Notification> getAllNotifications(Long userId);
    public Notification updatePip(Notification notification);
}
