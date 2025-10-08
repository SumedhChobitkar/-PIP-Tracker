package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.SkillGapAnalysisNotfoundException;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.SkillGapAnalysisRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.SkillGapAnalysisService;
import com.pipTracker.Validations.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class skillgapserviceimpl implements SkillGapAnalysisService {

    @Autowired
    private SkillGapAnalysisRepository skillGapRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(skillgapserviceimpl.class);

    @Override
    public SkillGapAnalysis addSkillGap(Long employeeId, SkillGapAnalysis skillGap)
    {
        try{
            validationskill(skillGap);
            Optional<Employee> option=employeeRepo.findById(employeeId);
            if (option.isPresent()) {
                Employee emp=option.get();
                skillGap.setEmployee(emp);
                skillGap.setRequiredLevel(10-skillGap.getCurrentLevel());
                skillGap.setGapLevel(10-skillGap.getRequiredLevel());
                SkillGapAnalysis sg=skillGapRepo.save(skillGap);

                AuditLog log=new AuditLog();
                log.setUserId(employeeId);
                log.setEntityId(sg.getAnalysisId());
                log.setTimestamp(LocalDateTime.now());
                log.setEntityname(EntityName.SKILLGAP);
                log.setAction(Action.CREATE);
                log.setRemarks("SkillGap Created");
                auditLogService.createAuditLogSkillGap(log);
                return sg;
            }
            else {
                throw new SkillGapAnalysisNotfoundException("Employee Not Found with this EmployeeId:"+employeeId);
            }
        }
        catch(SkillGapAnalysisNotfoundException e)
        {
            logger.info("Error :"+e.getMessage());
            throw e;
        }
    }

    @Override
    public List<SkillGapAnalysis> getSkillGapsByEmployee(Long employeeId)
    {
        try {
            return skillGapRepo.findByEmployee_EmployeeId(employeeId);
        }
        catch (SkillGapAnalysisNotfoundException e)
        {
            logger.info("error:"+e.getMessage());
            throw e;
        }
    }
    @Override
    public List<SkillGapAnalysis> getAllSkillGapsByManager(Long managerId)
    {
        try {
            List<Employee> employees = employeeRepo.findByManagerId(managerId);
            if (employees.isEmpty())
            {
                throw new SkillGapAnalysisNotfoundException("No Manager Found with "+managerId);
            }
            List<SkillGapAnalysis> result = new ArrayList<>();

            for (Employee emp : employees)
            {
                result.addAll(emp.getSkillGaps());
            }
            return result;
        } catch (SkillGapAnalysisNotfoundException e) {
            logger.info("error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public List<SkillGapAnalysis> getAllSkillGapsByHR(Long hrId) {
        try {
            List<Employee> employees = employeeRepo.findByHrId(hrId);
            if (employees.isEmpty())
            {
                throw new SkillGapAnalysisNotfoundException("No HR Found with "+hrId);
            }
            List<SkillGapAnalysis> result = new ArrayList<>();

            for (Employee emp : employees)
            {
                result.addAll(emp.getSkillGaps());
            }
            return result;
        } catch (SkillGapAnalysisNotfoundException e) {
            logger.info("error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public SkillGapAnalysis updateSkillGap(Long employeeId, SkillGapAnalysis skillGap)
    {
        try {
            validationskill(skillGap);
            Long skillId = skillGap.getAnalysisId();
            if (skillId == null) {
                throw new IllegalArgumentException("Analysis ID is required in the request body");
            }
            SkillGapAnalysis existing = skillGapRepo.findById(skillId).orElseThrow();

            if (!existing.getEmployee().getEmployeeId().equals(employeeId)) {
                throw new RuntimeException("SkillGapAnalysis does not belong to the specified employee");
            }
            existing.setSkill(skillGap.getSkill());
            existing.setCurrentLevel(skillGap.getCurrentLevel());
            skillGap.setRequiredLevel(10-skillGap.getCurrentLevel());
            skillGap.setGapLevel(10-skillGap.getRequiredLevel());
            existing.setSuggestedTraining(skillGap.getSuggestedTraining());
            SkillGapAnalysis sg=skillGapRepo.save(existing);


            AuditLog log = new AuditLog();
            log.setUserId(employeeId);
            log.setEntityname(EntityName.SKILLGAP);
            log.setEntityId(skillGap.getAnalysisId());
            log.setAction(Action.UPDATE);
            log.setTimestamp(LocalDateTime.now());
            log.setRemarks("SkillGap updated");
            auditLogService.updateAuditlogSkillGap(log);

            return sg;
        } catch (SkillGapAnalysisNotfoundException ex) {
            logger.info("SkillGap not found:"+ ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteAllSkillGapByEmployee(Long employeeId)
    {
        try {
            List<SkillGapAnalysis> skillGapAnalysisList = skillGapRepo.findByEmployee_EmployeeId(employeeId);

            if (skillGapAnalysisList.isEmpty()) {
                throw new SkillGapAnalysisNotfoundException("No SkillGap found for employee ID: " + employeeId);
            }

            skillGapRepo.deleteAll(skillGapAnalysisList);
        }
        catch(SkillGapAnalysisNotfoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void deleteSkillGapByEmployee(Long employeeId,Long analysisId)
    {
        try {
            Optional<SkillGapAnalysis> option=skillGapRepo.findById(analysisId);
            if(option.isPresent())
            {
                SkillGapAnalysis s=option.get();
                skillGapRepo.delete(s);

                AuditLog log = new AuditLog();
                log.setUserId(employeeId);
                log.setEntityname(EntityName.SKILLGAP);
                log.setEntityId(analysisId);
                log.setAction(Action.DELETE);
                log.setTimestamp(LocalDateTime.now());
                log.setRemarks("SkillGap deleted");
                auditLogService.createAuditLogSkillGap(log);
            }
            else {
                throw new SkillGapAnalysisNotfoundException("analysis ID NOT FOUND:"+analysisId);
            }
        }
        catch(SkillGapAnalysisNotfoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }

    public static void validationskill(SkillGapAnalysis skillGap)
    {
        if(skillGap.getSkill()==null || !Validation.skill_PATTERN.matcher(skillGap.getSkill()).matches())
        {
            throw new IllegalArgumentException("Input Valid Data");
        }
        if(Validation.currentLevel_PATTERN.matcher(String.valueOf(skillGap.getCurrentLevel())).matches())
        {
            throw new IllegalArgumentException("Input only 1 to 10 values");
        }
        if(skillGap.getSuggestedTraining()==null || !Validation.training_PATTERN.matcher(skillGap.getSuggestedTraining()).matches())
        {
            throw new IllegalArgumentException("Input Valid Data");
        }
    }
}
