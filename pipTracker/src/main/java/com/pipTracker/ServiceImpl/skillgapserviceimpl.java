package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.SkillGapAnalysis;
import com.pipTracker.Exception.SkillGapAnalysisNotfoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.SkillGapAnalysisRepository;
import com.pipTracker.Service.SkillGapAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private static final Logger logger = LoggerFactory.getLogger(FeedBackServiceImpl.class);

    @Override
    public SkillGapAnalysis addSkillGap(Long employeeId, SkillGapAnalysis skillGap)
    {
        try{
            Optional<Employee> option=employeeRepo.findById(employeeId);
            if (option.isPresent()) {
                Employee emp=option.get();
                skillGap.setEmployee(emp);
                skillGap.setGapLevel(skillGap.getRequiredLevel() - skillGap.getCurrentLevel());
                return skillGapRepo.save(skillGap);
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
            Long skillId = skillGap.getAnalysisId();
            if (skillId == null) {
                throw new IllegalArgumentException("Analysis ID is required in the request body");
            }
            SkillGapAnalysis existing = skillGapRepo.findById(skillId).orElseThrow();

            if (!existing.getEmployee().getEmployeeId().equals(employeeId)) {
                throw new RuntimeException("SkillGapAnalysis does not belong to the specified employee");
            }
            existing.setSkill(skillGap.getSkill());
            existing.setRequiredLevel(skillGap.getRequiredLevel());
            existing.setCurrentLevel(skillGap.getCurrentLevel());
            existing.setGapLevel(skillGap.getGapLevel());
            existing.setSuggestedTraining(skillGap.getSuggestedTraining());
            return skillGapRepo.save(existing);

        } catch (SkillGapAnalysisNotfoundException ex) {
            logger.info("Feedback not found:"+ ex.getMessage());
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

}



