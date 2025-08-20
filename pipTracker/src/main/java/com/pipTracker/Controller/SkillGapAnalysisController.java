package com.pipTracker.Controller;

import com.pipTracker.Entity.SkillGapAnalysis;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Exception.SkillGapAnalysisNotfoundException;
import com.pipTracker.Service.SkillGapAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/skillgaps")
public class SkillGapAnalysisController {

    @Autowired
    private SkillGapAnalysisService skillGapService;

    @PostMapping("/add/{employeeId}")
    public ResponseEntity<?> addSkillGap(@PathVariable Long employeeId, @RequestBody SkillGapAnalysis skillGap) {
        try {
            SkillGapAnalysis sk=skillGapService.addSkillGap(employeeId,skillGap);
            return ResponseEntity.status(HttpStatus.CREATED).body("SkillGap Added");
        }
        catch (SkillGapAnalysisNotfoundException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID Not Found with "+employeeId);
        }
    }

    @GetMapping("/get/{employeeId}")//get all analysis with employeeId
    public ResponseEntity<?> getSkillGapsByEmployee(@PathVariable Long employeeId) {
        try {
            List<SkillGapAnalysis> list=skillGapService.getSkillGapsByEmployee(employeeId);
            if(list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("SkillGap Not Available for employeeid:"+employeeId);
            }
            else
            {
                return ResponseEntity.ok(list);
            }
        }
        catch (FeedBackNotFoundException e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getSkillgapsByManagerId/{managerId}")
    public ResponseEntity<?> getAllSkillGapsByManager(@PathVariable Long managerId)
    {
        try {
            List<SkillGapAnalysis> list = skillGapService.getAllSkillGapsByManager(managerId);
            if (list.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No SkillGap available for employees under managerId: " + managerId);
            }
            else
            {
                return ResponseEntity.ok(list);
            }
        } catch (SkillGapAnalysisNotfoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Manager is Not Available with id " + managerId);
        }
    }

    @GetMapping("/getSkillgapsByHrId/{hrId}")
    public ResponseEntity<?> getAllSkillGapsByHR(@PathVariable Long hrId)
    {
        try {
            List<SkillGapAnalysis> list = skillGapService.getAllSkillGapsByHR(hrId);
            if (list.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No SkillGap available for employees under hrId: " + hrId);
            }
            else
            {
                return ResponseEntity.ok(list);
            }
        } catch (SkillGapAnalysisNotfoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("HR is Not Available with id " + hrId);
        }
    }

    @PutMapping("/update/{employeeId}")            // We need to mention which Analysis id should update in Json request body
    public ResponseEntity<?> updateSkillGap(@PathVariable Long employeeId, @RequestBody SkillGapAnalysis skillGap) {
        try
        {
            SkillGapAnalysis sk=skillGapService.updateSkillGap(employeeId,skillGap);
            return ResponseEntity.ok(sk);
        }
        catch (SkillGapAnalysisNotfoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SkillGap not found with ID: " + employeeId);
        }
    }

    @DeleteMapping("/delete/{employeeId}")//Delete all Analysis which is related to employee mention in URL
    public ResponseEntity<String> deleteAllSkillGapByEmployee(@PathVariable Long employeeId) {
        try {
            skillGapService.deleteAllSkillGapByEmployee(employeeId);
            return ResponseEntity.ok("All Analysis deleted for employee ID " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete analysis for employee ID: " + employeeId);
        }
    }

    @DeleteMapping("/delete/{employeeId}/{analysisId}")//Delete specific Analysis which is related to employeeId mention in URL and AnalysisId
    public ResponseEntity<String> deleteSkillGapByEmployee(@PathVariable Long employeeId,@PathVariable Long analysisId) {
        try {
            skillGapService.deleteSkillGapByEmployee(employeeId,analysisId);
            return ResponseEntity.ok("Analysis deleted for employee ID " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete analysis for employee ID:" + employeeId+"AnalysisId:"+analysisId);
        }
    }
}
