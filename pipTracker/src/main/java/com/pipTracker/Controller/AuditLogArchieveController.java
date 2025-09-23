package com.pipTracker.Controller;

import com.pipTracker.Entity.AuditLogArchieve;
import com.pipTracker.Exception.AuditLogArchieveNotFoundException;
import com.pipTracker.Service.AuditLogArchieveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Audit Log Archive APIs",
        description = "Operations related to Audit Log Archiving and Restoration"
)
@RestController
@CrossOrigin("*")
@RequestMapping("/api/auditlogarchieve")
public class AuditLogArchieveController {

    @Autowired
    private AuditLogArchieveService auditLogArchieveService;

    @Operation(
            summary = "Get Archived Audit Logs by Log ID",
            description = "Fetches all archived audit logs for a given logId.\n\n" +
                    "Eg: GET http://localhost:8080/api/auditlogarchieve/get/by/logid/{logId}"
    )
    @ApiResponse(responseCode = "200", description = "Archived logs fetched successfully")
    @ApiResponse(responseCode = "500", description = "Error occurred while fetching archived logs")
    @GetMapping("/get/by/logid/{logId}")
    public ResponseEntity<?> getAllArchieve(@PathVariable Long logId) {
        try {
            return ResponseEntity.ok(auditLogArchieveService.getAllArchieve(logId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID not found");
        }
    }

    @Operation(
            summary = "Restore Archived Audit Log by Log ID",
            description = "Restores an archived audit log back to the main Audit Log table.\n\n" +
                    "Eg: PUT http://localhost:8080/api/auditlogarchieve/restore/by/logid/{logId}"
    )
    @ApiResponse(responseCode = "202", description = "Audit log restored successfully")
    @ApiResponse(responseCode = "400", description = "Audit log already restored or not found")
    @PutMapping("/restore/by/logid/{logId}")
    public ResponseEntity<?> updateArchieve(@PathVariable Long logId) {
        try {
            AuditLogArchieve log = auditLogArchieveService.updateArchieve(logId);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Data of logId " + logId + " restored in AuditLog!");
        } catch (AuditLogArchieveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ID already restored in Audit Log");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Id not found in AuditLog");
        }
    }

    @Operation(
            summary = "Delete All Archived Audit Logs",
            description = "Deletes all entries from the Audit Log Archive table.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/auditlogarchieve/deleteall"
    )
    @ApiResponse(responseCode = "200", description = "All archived logs deleted successfully")
    @ApiResponse(responseCode = "500", description = "Error occurred while deleting")
    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAll() {
        try {
            auditLogArchieveService.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).body("DELETED SUCCESSFULLY!!!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while deleting!!!");
        }
    }

    @Operation(
            summary = "Delete Archived Audit Log by ID",
            description = "Deletes a specific archived audit log by its logId.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/auditlogarchieve/delete/by/id/{logId}"
    )
    @ApiResponse(responseCode = "200", description = "Archived log deleted successfully")
    @ApiResponse(responseCode = "500", description = "Error occurred while deleting")
    @DeleteMapping("/delete/by/id/{logId}")
    public ResponseEntity<?> deleteAuditLogArchieve(@PathVariable Long logId) {
        try {
            auditLogArchieveService.deleteAuditLogArchieve(logId);
            return ResponseEntity.status(HttpStatus.OK).body("DELETED SUCCESSFULLY!!!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID Not Found!!!!");
        }
    }
}
