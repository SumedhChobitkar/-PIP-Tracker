package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Notification;
import com.pipTracker.Exception.AuditLogNotFoundException;
import com.pipTracker.Exception.NotificationNotFoundException;
import com.pipTracker.Repository.NotificationRepository;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.Validations.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements Notificationservice {
    @Autowired
    private NotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public Notification createNotification(Notification notification) {
        try {
            validationNotification(notification);
            System.out.println("Notification Sent Successfully");
            return notificationRepository.save(notification);
        }
        catch (Exception e)
        {
            logger.info("Error"+e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId)
    {
        try {
             List list=notificationRepository.findByUserIdAndIsReadFalse(userId);
             if(list.isEmpty())
             {
                 throw new NotificationNotFoundException("ID Not Found with Notification");
             }
             return list;
        }
        catch (NotificationNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public void markAsRead(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException("Notification id not found:"+notificationId));

            notification.setIsRead(true);
            notificationRepository.save(notification);
        } catch (NotificationNotFoundException e) {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Notification> getAllNotifications(Long userId) {
        try {
             List list =notificationRepository.findByUserId(userId);
             if(list.isEmpty())
             {
                 throw new NotificationNotFoundException("Notification Not Found with Id "+userId);
             }
             return list;
        }
        catch (NotificationNotFoundException e)
        {
            logger.info("Error"+e.getMessage());
            throw e;
        }
    }
    @Override
    public Notification updatePip(Notification notification) {
        try {
            return notificationRepository.save(notification);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }
    public static void validationNotification(Notification notification)
    {
        if(notification.getUserId()==null || !Validation.useridN_PATTERN.matcher(notification.getUserId().toString()).matches())
        {
            throw new IllegalArgumentException("Add only Valid id Number");
        }
        if(notification.getTitle()==null || !Validation.title_PATTERN.matcher(notification.getTitle()).matches())
        {
            throw new IllegalArgumentException("Input Valid Title");
        }
        if(notification.getMessage()==null || !Validation.message_PATTERN.matcher(notification.getMessage()).matches())
        {
            throw new IllegalArgumentException("Add message VAlid");
        }
        if(notification.getType()==null || !Validation.type_PATTERN.matcher(notification.getType()).matches())
        {
            throw new IllegalArgumentException("Add Valid Type");
        }
    }
}
