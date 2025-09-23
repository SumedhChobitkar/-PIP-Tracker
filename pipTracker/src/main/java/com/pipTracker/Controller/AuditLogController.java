package com.pipTracker.Controller;

import com.pipTracker.Entity.AuditLog;
import com.pipTracker.Exception.AuditLogNotFoundException;
import com.pipTracker.Service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Audit Log APIs",
        description = "Operations related to Audit Logs"
)
@RestController
@CrossOrigin("*")
@RequestMapping("/api/auditlog")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Operation(
            summary = "Get All Audit Logs",
            description = "Fetches all audit logs stored in the system.\n\n" +
                    "Eg: GET http://localhost:8080/api/auditlog/get/for/all"
    )
    @ApiResponse(responseCode = "200", description = "Audit logs fetched successfully")
    @ApiResponse(responseCode = "202", description = "No audit logs available")
    @ApiResponse(responseCode = "404", description = "Audit logs not found")
    @GetMapping("/get/for/all")
    public ResponseEntity<?> getAllLogs() {
        try {
            List<AuditLog> list = auditLogService.getAllLogs();
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("AuditLog Not Available");
            } else {
                return ResponseEntity.ok(list);
            }
        } catch (AuditLogNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Get Audit Logs by Entity ID",
            description = "Fetches audit logs filtered by a specific entityId.\n\n" +
                    "Eg: GET http://localhost:8080/api/auditlog/get/auditlog/by/entityid/{entityId}"
    )
    @ApiResponse(responseCode = "200", description = "Audit logs fetched successfully")
    @ApiResponse(responseCode = "202", description = "No audit logs available for given entityId")
    @ApiResponse(responseCode = "404", description = "Audit logs not found")
    @GetMapping("/get/auditlog/by/entityid/{entityId}")
    public ResponseEntity<?> getFeedBackLogsById(@PathVariable Long entityId) {
        try {
            List<AuditLog> list = auditLogService.getFeedBackLogsById(entityId);
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("AuditLog Not Available");
            } else {
                return ResponseEntity.ok(list);
            }
        } catch (AuditLogNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Delete Audit Log by Log ID",
            description = "Deletes a specific audit log entry using its logId.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/auditlog/delete/by/logid/{logId}"
    )
    @ApiResponse(responseCode = "202", description = "Audit log deleted successfully")
    @ApiResponse(responseCode = "500", description = "Error occurred while deleting")
    @DeleteMapping("/delete/by/logid/{logId}")
    public ResponseEntity<String> deleteAuditLog(@PathVariable Long logId) {
        try {
            auditLogService.deleteAuditLog(logId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Id not Found");
        }
    }
}
