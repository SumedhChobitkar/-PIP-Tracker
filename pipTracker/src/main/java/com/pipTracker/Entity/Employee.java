package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;
    private Long managerId;// we need to change after completion of code
    private Long hrId;
    private String photoUrl;
    private LocalDate joiningDate;
    private String status;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("employee")
    private User user;

    @OneToMany(mappedBy = "employee",cascade=CascadeType.ALL)
    @JsonManagedReference
    private List<FeedBack> feedback=new ArrayList<>();

}
