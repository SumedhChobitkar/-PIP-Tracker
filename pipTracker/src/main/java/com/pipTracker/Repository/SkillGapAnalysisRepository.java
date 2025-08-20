package com.pipTracker.Repository;

import com.pipTracker.Entity.SkillGapAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillGapAnalysisRepository extends JpaRepository<SkillGapAnalysis, Long> {
    List<SkillGapAnalysis> findByEmployee_EmployeeId(Long employeeId);
}

