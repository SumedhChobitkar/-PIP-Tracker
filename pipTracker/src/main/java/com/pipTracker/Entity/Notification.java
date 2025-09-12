package com.pipTracker.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class Notification
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long userId;

    private String title;

    private String message;

    private String type; // e.g., REMINDER, ALERT, INFO

    private Boolean isRead = false;

    private LocalDateTime timestamp;
}
