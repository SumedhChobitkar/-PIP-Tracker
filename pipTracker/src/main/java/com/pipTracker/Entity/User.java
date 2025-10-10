package com.pipTracker.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder  // <-- Add this for builder support
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;
    private String password;
    private boolean recentlyChangedPassword = false;
    private LocalDateTime lastPasswordChangedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;
    private Long managerId;

    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    private Status status;
    private String Isregistered;

    private String status;
    private String isRegistered; // <-- corrected naming

    @Lob
    @JsonIgnore   // prevent returning raw bytes in normal API responses
    private byte[] photoUrl;
    private String fileType;
    private Long fileSize;   // optional but useful

    private String otp;
    private LocalDateTime otpExpiry;

    private LocalDateTime lastLoginTime;

    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties("user")
    private Employee employee;

    public CharSequence getIsregistered() {
        return null;
    }
}
