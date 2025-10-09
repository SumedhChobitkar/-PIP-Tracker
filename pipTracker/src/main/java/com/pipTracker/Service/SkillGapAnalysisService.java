package com.pipTracker.Service;

import com.pipTracker.Entity.SkillGapAnalysis;

import java.util.List;

public interface SkillGapAnalysisService {
    SkillGapAnalysis addSkillGap(Long employeeId, SkillGapAnalysis skillGap);
    List<SkillGapAnalysis> getSkillGapsByEmployee(Long employeeId);
    List<SkillGapAnalysis> getAllSkillGapsByManager(Long managerId);
    List<SkillGapAnalysis> getAllSkillGapsByHR(Long hrId);
    SkillGapAnalysis updateSkillGap(Long employeeId, SkillGapAnalysis skillGap);
    void deleteAllSkillGapByEmployee(Long employeeId) ;
    void deleteSkillGapByEmployee(Long employeeId,Long analysisId) ;
}
