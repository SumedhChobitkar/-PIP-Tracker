package com.pipTracker.Controller;

import com.pipTracker.Entity.AuditLogArchieve;
import com.pipTracker.Exception.AuditLogArchieveNotFoundException;
import com.pipTracker.Service.AuditLogArchieveService;
import com.pipTracker.Service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auditlogarchieve")
public class AuditLogArchieveController
{
    @Autowired
    private AuditLogArchieveService auditLogArchieveService;

    @GetMapping("/get/by/logid/{logId}")
    public ResponseEntity<?> getAllArchieve(@PathVariable Long logId)
    {
        try
        {
               return ResponseEntity.ok(auditLogArchieveService.getAllArchieve(logId));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID not found");
        }
    }
    @PutMapping("/restore/by/logid/{logId}")
    public ResponseEntity<?> updateArchieve(@PathVariable Long logId)
    {
        try
        {
            AuditLogArchieve log=auditLogArchieveService.updateArchieve(logId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Data of logId "+logId+" restore in AuditLog!!!!!");
        }
        catch (AuditLogArchieveNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID ALREADY RESTORED IN AUDIT LOG");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found of auditLog");
        }
    }
    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAll()
    {
        try
        {
            auditLogArchieveService.deleteAll();;
            return ResponseEntity.status(HttpStatus.OK).body("DELETED SUCCESSFULLY!!!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to delete!!!!");
        }
    }
    @DeleteMapping("/delete/by/id/{logId}")
    public ResponseEntity<?> deleteAuditLogArchieve(@PathVariable Long logId)
    {
        try
        {
            auditLogArchieveService.deleteAuditLogArchieve(logId);
            return ResponseEntity.status(HttpStatus.OK).body("DELETED SUCCESSFULLY!!!");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID Not Found!!!!");
        }
    }
}
