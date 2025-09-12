package com.pipTracker.Controller;

import com.pipTracker.Entity.AuditLog;
import com.pipTracker.Exception.AuditLogNotFoundException;
import com.pipTracker.Service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auditlog")
public class AuditLogController
{
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/get/for/all")
    public ResponseEntity<?> getAllLogs() {
        {
            try {
                List<AuditLog> list=auditLogService.getAllLogs();
                if(list.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body("AuditLog Not Available");
                }
                else
                {
                    return ResponseEntity.ok(list);
                }
            }
            catch (AuditLogNotFoundException e)
            {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }
    @GetMapping("/get/auditlog/by/entityid/{entityId}")
    public ResponseEntity<?> getFeedBackLogsById(@PathVariable Long entityId) {
        {
            try {
                List<AuditLog> list=auditLogService.getFeedBackLogsById(entityId);
                if(list.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body("AuditLog Not Available");
                }
                else
                {
                    return ResponseEntity.ok(list);
                }
            }
            catch (AuditLogNotFoundException e)
            {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }
    @DeleteMapping("/delete/by/logid/{logId}")
    public ResponseEntity<String> deleteAuditLog(@PathVariable Long logId)
    {
        try
        {
           auditLogService.deleteAuditLog(logId);
           return ResponseEntity.status(HttpStatus.ACCEPTED).body("Deleted Successfully");
        }
        catch (Exception e)
        {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Id not Found");
        }
    }
}
