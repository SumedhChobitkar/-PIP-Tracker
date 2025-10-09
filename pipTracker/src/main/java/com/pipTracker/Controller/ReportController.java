/*
package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.Report;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reports")
@Tag(name = "Report APIs", description = "CRUD operations and file handling for reports")
public class ReportController {

   /* @Autowired
    private ReportService reportService;
    @Autowired
    private  ObjectMapper mapper; // Injected
*/
    private final ObjectMapper mapper;
    private final ReportService reportService;

    // ✅ Constructor injection
    public ReportController(ObjectMapper mapper, ReportService reportService) {
        this.mapper = mapper;
        this.reportService = reportService;
    }

    // Create a new report (with file upload)
    @Operation(
            summary = "Create a Report",
            description = "Creates a new report for an employee. Supports file upload along with report details.\n\n" +
                    "Eg: POST http://localhost:8080/api/reports/create/{employeeId}/reports"
    )
    @ApiResponse(responseCode = "200", description = "Report created successfully")
    @ApiResponse(responseCode = "400", description = "Error while creating report")
    @PostMapping(
            value = "/create/{employeeId}/reports",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
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

    // Get all reports
    @Operation(
            summary = "Get All Reports",
            description = "Fetches all available reports.\n\nEg: GET http://localhost:8080/api/reports"
    )
    @ApiResponse(responseCode = "200", description = "Reports fetched successfully")
    @GetMapping
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

    // Get report by ID
    @Operation(
            summary = "Get Report by ID",
            description = "Fetches a single report by its ID.\n\nEg: GET http://localhost:8080/api/reports/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Report fetched successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @GetMapping("/{id}")
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

    // Get all reports for an employee
    @Operation(
            summary = "Get Reports by Employee ID",
            description = "Fetches all reports linked to a specific employee.\n\nEg: GET http://localhost:8080/api/reports/employee/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Reports fetched successfully")
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

    // Get Employee Image by ID or Name
    @Operation(
            summary = "Get Employee Image",
            description = "Fetches employee image by employeeId or name.\n\nEg: GET http://localhost:8080/api/reports/get-image?employeeId=1"
    )
    @ApiResponse(responseCode = "200", description = "Image fetched successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
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

    // Update report details
    @Operation(
            summary = "Update Report",
            description = "Updates a report's details by its ID.\n\nEg: PUT http://localhost:8080/api/reports/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Report updated successfully")
    @PutMapping("/{id}")
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

    // Update Report Image by Report ID
    @Operation(
            summary = "Update Report Image by Report ID",
            description = "Updates the image attached to a specific report.\n\nEg: PUT http://localhost:8080/api/reports/update-image/{reportId}"
    )
    @ApiResponse(responseCode = "200", description = "Image updated successfully")
    @PutMapping(
            value = "/update-image/{reportId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
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

    // Update Report Image by Employee ID
    @Operation(
            summary = "Update Image by Employee ID",
            description = "Updates employee's image using their employeeId.\n\nEg: PUT http://localhost:8080/api/reports/update-image/employee/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Image updated successfully")
    @PutMapping(
            value = "/update-image/employee/{employeeId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
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

    // Delete report by ID
    @Operation(
            summary = "Delete Report",
            description = "Deletes a report by its ID.\n\nEg: DELETE http://localhost:8080/api/reports/{reportId}"
    )
    @ApiResponse(responseCode = "200", description = "Report deleted successfully")
    @DeleteMapping("/{reportId}")
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
*/

/*package com.pipTracker.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.Report;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reports")
@Tag(name = "Report APIs", description = "CRUD operations and file handling for reports")
public class ReportController {

    /* @Autowired
     private ReportService reportService;
     @Autowired
     private  ObjectMapper mapper; // Injected
 */
    /*private final ObjectMapper mapper;
    private final ReportService reportService;

    // Constructor injection
    public ReportController(ObjectMapper mapper, ReportService reportService) {
        this.mapper = mapper;
        this.reportService = reportService;
    }

    @Operation(
            summary = "Create a Report",
            description = "Creates a new report for an employee. Supports file upload along with report details.\n\n" +
                    "Eg: POST http://localhost:8080/api/reports/create/{employeeId}/reports"
    )
    @ApiResponse(responseCode = "200", description = "Report created successfully")
    @ApiResponse(responseCode = "400", description = "Error while creating report")
    @PostMapping("/create/{employeeId}/reports")
    public ResponseEntity<?> createReport(
            @PathVariable Long employeeId,
            @RequestParam("reportData") String reportData,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            //ObjectMapper mapper = new ObjectMapper();

            Report report = mapper.readValue(reportData, Report.class);

            Report savedReport = reportService.createReport(report, employeeId, file);
            return ResponseEntity.ok(savedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get All Reports",
            description = "Fetches all available reports.\n\n" +
                    "Eg: GET http://localhost:8080/api/reports"
    )
    @ApiResponse(responseCode = "200", description = "Reports fetched successfully")
    @ApiResponse(responseCode = "404", description = "No reports found")
    @GetMapping
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

    @Operation(
            summary = "Get Report by ID",
            description = "Fetches a single report by its ID.\n\n" +
                    "Eg: GET http://localhost:8080/api/reports/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Report fetched successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @GetMapping("/{id}")
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

    @Operation(
            summary = "Get Reports by Employee ID",
            description = "Fetches all reports linked to a specific employee.\n\n" +
                    "Eg: GET http://localhost:8080/api/reports/employee/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Reports fetched successfully")
    @ApiResponse(responseCode = "500", description = "Error while fetching reports")
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

    @Operation(
            summary = "Get Employee Image",
            description = "Fetches employee image by employeeId or name.\n\n" +
                    "Eg: GET http://localhost:8080/api/reports/get-image?employeeId=1"
    )
    @ApiResponse(responseCode = "200", description = "Image fetched successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
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

    @Operation(
            summary = "Update Report",
            description = "Updates a report's details by its ID.\n\n" +
                    "Eg: PUT http://localhost:8080/api/reports/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Report updated successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @PutMapping("/{id}")
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

    @Operation(
            summary = "Update Report Image by Report ID",
            description = "Updates the image attached to a specific report.\n\n" +
                    "Eg: PUT http://localhost:8080/api/reports/update-image/{reportId}"
    )
    @ApiResponse(responseCode = "200", description = "Image updated successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
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

    @Operation(
            summary = "Update Image by Employee ID",
            description = "Updates employee's image using their employeeId.\n\n" +
                    "Eg: PUT http://localhost:8080/api/reports/update-image/employee/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Image updated successfully")
    @ApiResponse(responseCode = "400", description = "Error while updating image")
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

    @Operation(
            summary = "Delete Report",
            description = "Deletes a report by its ID.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/reports/{reportId}"
    )
    @ApiResponse(responseCode = "200", description = "Report deleted successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @DeleteMapping("/{reportId}")
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
*/

package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.Report;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reports")
@Tag(name = "Report APIs", description = "CRUD operations and file handling for reports")
public class ReportController {

    private final ObjectMapper mapper;
    private final ReportService reportService;

    //Constructor Injection (Best Practice)
    public ReportController(ObjectMapper mapper, ReportService reportService) {
        this.mapper = mapper;
        this.reportService = reportService;
    }

    //Create Report
    @Operation(
            summary = "Create a Report",
            description = "Creates a new report for an employee. Supports file upload along with report details.\n\n" +
                    "Eg: POST http://localhost:8080/api/reports/create/{employeeId}/reports"
    )
    @ApiResponse(responseCode = "200", description = "Report created successfully")
    @ApiResponse(responseCode = "400", description = "Error while creating report")
    @PostMapping(value = "/create/{employeeId}/reports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReport(
            @PathVariable Long employeeId,
            @RequestParam("reportData") String reportData,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Report report = mapper.readValue(reportData, Report.class);
            Report savedReport = reportService.createReport(report, employeeId, file);
            return ResponseEntity.ok(savedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    //Get All Reports
    @Operation(
            summary = "Get All Reports",
            description = "Fetches all available reports.\n\nEg: GET http://localhost:8080/api/reports"
    )
    @GetMapping
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

    //Get Report by ID
    @Operation(
            summary = "Get Report by ID",
            description = "Fetches a single report by its ID.\n\nEg: GET http://localhost:8080/api/reports/{id}"
    )
    @GetMapping("/{id}")
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

    //Get Reports by Employee ID
    @Operation(
            summary = "Get Reports by Employee ID",
            description = "Fetches all reports linked to a specific employee.\n\nEg: GET http://localhost:8080/api/reports/employee/{employeeId}"
    )
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

    //Get Employee Image
    @Operation(
            summary = "Get Employee Image",
            description = "Fetches employee image by employeeId or name.\n\nEg: GET http://localhost:8080/api/reports/get-image?employeeId=1"
    )
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

    //Update Report (Details)
    @Operation(
            summary = "Update Report",
            description = "Updates a report's details by its ID.\n\nEg: PUT http://localhost:8080/api/reports/{id}"
    )
    @PutMapping("/{id}")
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

    //Update Report Image by Report ID
    @Operation(
            summary = "Update Report Image by Report ID",
            description = "Updates the image attached to a specific report.\n\nEg: PUT http://localhost:8080/api/reports/update-image/{reportId}"
    )
    @PutMapping(value = "/update-image/{reportId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    // Update Image by Employee ID
    @Operation(
            summary = "Update Image by Employee ID",
            description = "Updates employee's image using their employeeId.\n\nEg: PUT http://localhost:8080/api/reports/update-image/employee/{employeeId}"
    )
    @PutMapping(value = "/update-image/employee/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    //Delete Report
    @Operation(
            summary = "Delete Report",
            description = "Deletes a report by its ID.\n\nEg: DELETE http://localhost:8080/api/reports/{reportId}"
    )
    @DeleteMapping("/{reportId}")
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

