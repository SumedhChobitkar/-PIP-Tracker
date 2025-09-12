package com.pipTracker.ServiceImpl;


import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.ReportRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Report createReport(Report report, Long employeeId, MultipartFile file) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
            report.setEmployee(employee);

            report.setGeneratedOn(LocalDateTime.now());

            AuditLog log=new AuditLog();
            log.setUserId(report.getCreatedBy());
            log.setEntityId(report.getReportId());
            log.setTimestamp(LocalDateTime.now());
            log.setEntityname(EntityName.REPORT);
            log.setAction(Action.CREATE);
            log.setRemarks("Report Created");
            auditLogService.createAuditLogFeedBack(log);

            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();

                if (isValidImageType(contentType)) {
                    report.setFile(file.getBytes());
                    report.setFileType(contentType);
                } else {
                    throw new RuntimeException("Invalid file format. Only JPEG, JPG, PNG allowed.");
                }
            }

            return reportRepository.save(report);

        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating report: " + e.getMessage());
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg")||
                contentType.equals("image/jpg")||
                contentType.equals("image/png");



    }
    @Override
    public List<Report>getAllReports(){
        try {
            return reportRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch reports",  e);
        }
    }

    @Override
    public Report getReportById(Long reportId) {
        try {
            return reportRepository.findById(reportId)
                    .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching report", e);
        }
    }



    @Override
    public List<Report> getReportsByEmployeeId(Long employeeId) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

            return reportRepository.findByEmployee_EmployeeId(employeeId);

        } catch (EmployeeNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching reports for employeeId: " + employeeId, e);
        }
    }

    @Override
    public byte[] getEmployeeImage(Long employeeId, String name) {
        List<Report> reports;

        if (employeeId != null && name != null) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            if (!employee.getName().equalsIgnoreCase(name)) {
                throw new RuntimeException("EmployeeId and Name do not match!");
            }

            reports = reportRepository.findByEmployee_EmployeeId(employeeId);

        } else if (employeeId != null) {
            reports = reportRepository.findByEmployee_EmployeeId(employeeId);

        } else if (name != null) {
            reports = reportRepository.findByEmployee_Name(name);

        } else {
            throw new RuntimeException("Please provide either EmployeeId or Name!");
        }

        for (Report report : reports) {
            if (report.getFile() != null) {
                return report.getFile();
            }
        }

        throw new RuntimeException("Image not found for given employee!");
    }


    @Override
    public Report updateReport(Long reportId, Report updated) {
        try {
            Report existing = reportRepository.findById(reportId)
                    .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));

            existing.setCreatedBy(updated.getCreatedBy());
            existing.setReportType(updated.getReportType());

            AuditLog log = new AuditLog();
            log.setUserId(updated.getCreatedBy());
            log.setEntityname(EntityName.REPORT);
            log.setEntityId(reportId);
            log.setAction(Action.UPDATE);
            log.setTimestamp(LocalDateTime.now());
            log.setRemarks("Report updated");
            auditLogService.updateAuditlogFeedBack(log);

            return reportRepository.save(existing);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating report", e);
        }
    }

    @Override
    public Report updateReportImage(Long reportId, MultipartFile file) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        try {
            report.setFile(file.getBytes());
            report.setFileType(file.getContentType());
            return reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

    @Override
    public Report updateImageByEmployeeId(Long employeeId, MultipartFile file) {
        List<Report> reports = reportRepository.findByEmployee_EmployeeId(employeeId);

        if (reports.isEmpty()) {
            throw new RuntimeException("No reports found for EmployeeId: " + employeeId);
        }

        Report report = reports.get(0);
        try {
            report.setFile(file.getBytes());
            report.setFileType(file.getContentType());
            return reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

        @Override
        public boolean deleteReport(Long reportId) {
            try {
                reportRepository.deleteById(reportId);
                AuditLog log = new AuditLog();
                log.setUserId(reportId);
                log.setEntityname(EntityName.REPORT);
                log.setEntityId(reportId);
                log.setAction(Action.DELETE);
                log.setTimestamp(LocalDateTime.now());
                log.setRemarks("Report deleted");
                auditLogService.createAuditLogFeedBack(log);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Error while deleting report", e);
            }
        }


    }













