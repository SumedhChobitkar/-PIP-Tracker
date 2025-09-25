package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.Report;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reports")
@Tag(name = "Report API", description = "CRUD operations for reports")

public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/create/{employeeId}/reports")
    public ResponseEntity<?> createReport(
            @PathVariable Long employeeId,
            @RequestParam("reportData") String reportData,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Report report = mapper.readValue(reportData, Report.class);

            Report savedReport = reportService.createReport(report, employeeId, file);
            return ResponseEntity.ok(savedReport);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    @GetMapping
    @Operation(summary = "Get all reports", description = "This API fetches all reports")
    public ResponseEntity<?> getAll() {
        try {
            List<Report> reports = reportService.getAllReports();
            if (reports.isEmpty()) {
                return new ResponseEntity<>("No reports found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while fetching reports", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get report by ID", description = "Fetch a report by its ID")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            Report report = reportService.getReportById(id);
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (ReportNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getReportsByEmployeeId(@PathVariable Long employeeId) {
        try {
            List<Report> reports = reportService.getReportsByEmployeeId(employeeId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get-image")
    public ResponseEntity<?> getEmployeeImage(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String name) {
        try {
            byte[] image = reportService.getEmployeeImage(employeeId, name);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update report", description = "Update report details by ID")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody Report updated) {
        try {
            Report report = reportService.updateReport(id, updated);
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (ReportNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update report", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-image/{reportId}")
    public ResponseEntity<?> updateReportImage(
            @PathVariable Long reportId,
            @RequestParam("file") MultipartFile file) {
        try {
            Report updatedReport = reportService.updateReportImage(reportId, file);
            return ResponseEntity.ok("Image updated successfully for ReportId: " + updatedReport.getReportId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating image!");
        }
    }

    @PutMapping("/update-image/employee/{employeeId}")
    public ResponseEntity<?> updateImageByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file) {
        try {
            Report updated = reportService.updateImageByEmployeeId(employeeId, file);
            return ResponseEntity.ok("Image updated successfully for Employee ID: " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    @DeleteMapping("/{reportId}")
    @Operation(summary = "Delete report", description = "Delete a report by ID")
    public ResponseEntity<?> delete(@PathVariable("reportId") Long reportId) {
        try {
            boolean deleted = reportService.deleteReport(reportId);
            if (deleted) {
                return new ResponseEntity<>("Report deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Delete failed", HttpStatus.BAD_REQUEST);
            }
        } catch (ReportNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while deleting report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}


