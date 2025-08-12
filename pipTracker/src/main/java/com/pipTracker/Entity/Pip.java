package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pip")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long pipId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String goals;

    @Column(length = 500)
    private String progress;

    @Enumerated(EnumType.STRING)
    //Fill enum as a text in DB
    private Status status;//ACTIVE,COMPLETED,FAILED

    private Long reviewerId;

    @Column(length = 500)
    private String outcome;

    @Column(length = 1000)
    private String comments;

    //Many PIps belong to one Employee
    @ManyToOne
    @JoinColumn(name = "employeeId",nullable = false)
    @JsonBackReference
    private Employee employee;

}