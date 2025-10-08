package com.pipTracker.Controller;

import com.pipTracker.Entity.SkillGapAnalysis;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Exception.SkillGapAnalysisNotfoundException;
import com.pipTracker.Service.SkillGapAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/skillgaps")
@Tag(name ="skill Gaps",description = "APIs for managing employee skill gap analysis")
public class SkillGapAnalysisController {

    @Autowired
    private SkillGapAnalysisService skillGapService;


    @Operation(summary = "Add Skill Gap",description = "Add a new skill gap for an employee.\n\n" +
            "Eg: POST http://localhost:8080/api/skillgaps/add/{employeeId}"
            )
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "SkillGap Added successfully"),
            @ApiResponse(responseCode = "400",description = "Employee ID not found")
    })
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
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @Operation(summary = "Get Skill Gaps by Employee",description = "Fetch all skill gaps For a specific employee.\n\n" +
            "Eg: GET http://localhost:8080/api/skillgaps/get/{employeeId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Skill gaps fetched successfully"),
            @ApiResponse(responseCode = "404",description = "Employee Not found")
    })
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

    @Operation(summary = "Get Skill Gaps by Manager",description = "Fetch all skill gaps for employees under a manager.\n\n" +
            "Eg:GET http://localhost:8080/api/skillgaps/getSkillgapsByManagerId{managerId}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Skill gaps fetched successfully"),
            @ApiResponse(responseCode = "404",description = "No skill gaps found for given manager")
    })
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


    @Operation(summary = "Get Skill Gaps by HR",description = "Fetch all skill gaps for employees under an HR.\n\n" +
            "Eg: GET http://localhost:8080/api/skillgaps/getSkillgapsByHrId/{hrId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "skill gaps fetched successfully"),
            @ApiResponse(responseCode = "404",description = "No skill gaps for given HR")
    })
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


    @Operation(summary = "Update Skill Gap",description = "Update an employee's skill gap analysis.\n\n" +
            "Eg: PUT http://localhost:8080/api/skillgaps/update/{employeeId}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Skill gap updated successfully"),
            @ApiResponse(responseCode = "404",description = "Skill gap not found")
    })
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @Operation(summary = "Delete All Skill Gaps by employee",description = "Delete all skill gap analysis records for an employee.\n\n" +
            "Eg: DELETE http://localhost:8080/api/skillgaps/delete/{employeeId}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "All skill gaps deleted successfully"),
            @ApiResponse(responseCode = "400",description = "Failed to delete skill gaps")
    })
    @DeleteMapping("/delete/{employeeId}")//Delete all Analysis which is related to employee mention in URL
    public ResponseEntity<String> deleteAllSkillGapByEmployee(@PathVariable Long employeeId) {
        try {
            skillGapService.deleteAllSkillGapByEmployee(employeeId);
            return ResponseEntity.ok("All Analysis deleted for employee ID " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete analysis for employee ID: " + employeeId);
        }
    }


    @Operation(summary = "Delete Specific Skill Gap",description = "Delete a specific skill gap by employee and analysis ID.\n\n" +
            "Eg:DELETE http://localhost:8080/api/skillgaps/delete/{employeeId}/{analysis}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Skill gap deleted successfully"),
            @ApiResponse(responseCode = "400",description = "Failed to delete skill gap")
    })
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
