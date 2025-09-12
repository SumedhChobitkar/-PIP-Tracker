package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table
public class SkillGapAnalysis {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"skillGaps", "feedback", "pip", "user"})
    private Employee employee;

    private String skill;
    private int requiredLevel;
    private int currentLevel;
    private int gapLevel;
    private String suggestedTraining;            //later may it will change to enum
}
