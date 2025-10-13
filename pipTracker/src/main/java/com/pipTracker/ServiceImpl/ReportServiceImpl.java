/*package com.pipTracker.ServiceImpl;


import com.pipTracker.CommonUtil.ValidationClass;
import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.ReportRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            validateReport(report);
            reportRepository.save(report);
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

    private void validateReport(Report report) {

        if (report.getCreatedBy() == null || !ValidationClass.ID_PATTERN.matcher(report.getCreatedBy().toString()).matches()) {
            throw new IllegalArgumentException("Invalid createdBy ID");
        }


        if (report.getEmployee() == null || report.getEmployee().getEmployeeId() == null ||
                !ValidationClass.ID_PATTERN.matcher(report.getEmployee().getEmployeeId().toString()).matches()) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        if (report.getFileType() != null &&
                !ValidationClass.FILE_TYPE_PATTERN.matcher(report.getFileType()).matches()) {
            throw new IllegalArgumentException("Invalid file type");
        }

        if (report.getGeneratedOn() == null) {
            throw new IllegalArgumentException("generatedOn cannot be null");


        if (report.getEmployee() == null || report.getEmployee().getEmployeeId() == null ||
                !ValidationClass.ID_PATTERN.matcher(report.getEmployee().getEmployeeId().toString()).matches()) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        if (report.getFileType() != null &&
                !ValidationClass.FILE_TYPE_PATTERN.matcher(report.getFileType()).matches()) {
            throw new IllegalArgumentException("Invalid file type");
        }

        if (report.getGeneratedOn() == null) {
            throw new IllegalArgumentException("generatedOn cannot be null");
        }
    }
*/
/*package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.ReportRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuditLogService auditLogService;

    // Create Report with Image Upload
    @Override
    public Report createReport(Report report, Long employeeId, MultipartFile file) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
            report.setEmployee(employee);
            report.setGeneratedOn(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();

                if (isValidImageType(contentType)) {
                    report.setFile(file.getBytes());
                    report.setFileType(contentType);
                    report.setFileName(file.getOriginalFilename());
                    report.setFileSize(file.getSize());
                } else {
                    throw new RuntimeException("Invalid file format. Only JPEG, JPG, PNG allowed.");
                }
            }

            Report savedReport = reportRepository.save(report);

            //  Generate accessible image URL
            String imageUrl = "http://localhost:8080/api/reports/image/" + savedReport.getReportId();
            savedReport.setImageUrl(imageUrl);

            // Create audit log
            AuditLog log = new AuditLog();
            log.setUserId(report.getCreatedBy());
            log.setEntityId(savedReport.getReportId());
            log.setTimestamp(LocalDateTime.now());
            log.setEntityname(EntityName.REPORT);
            log.setAction(Action.CREATE);
            log.setRemarks("Report Created");
            auditLogService.createAuditLogFeedBack(log);

            return reportRepository.save(savedReport);

        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating report: " + e.getMessage());
        }
    }

    //  Validate image formats
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png");
    }

    //  Get all reports
    @Override
    public List<Report> getAllReports() {
        try {
            List<Report> reports = reportRepository.findAll();
            for (Report r : reports) {
                r.setImageUrl("http://localhost:8080/api/reports/image/" + r.getReportId());
            }
            return reports;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch reports", e);
        }
    }

    //  Get report by ID
    @Override
    public Report getReportById(Long reportId) {
        try {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));

            report.setImageUrl("http://localhost:8080/api/reports/image/" + reportId);
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching report", e);
        }
    }

    //  Get all reports by Employee ID
    @Override
    public List<Report> getReportsByEmployeeId(Long employeeId) {
        try {
            employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

            List<Report> reports = reportRepository.findByEmployee_EmployeeId(employeeId);
            for (Report r : reports) {
                r.setImageUrl("http://localhost:8080/api/reports/image/" + r.getReportId());
            }
            return reports;
        } catch (EmployeeNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching reports for employeeId: " + employeeId, e);

        }
    }

    //  Get image by Employee ID or Name
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


}




    // Get report image by reportId (for /api/reports/image/{id})
    @Override
    public byte[] getReportImage(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));


        if (report.getFile() == null) {
            throw new RuntimeException("No image found for this report");
        }

        return report.getFile();
    }

    // Update report details
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

    // Update report image by reportId
    @Override
    public Report updateReportImage(Long reportId, MultipartFile file) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        try {
            report.setFile(file.getBytes());
            report.setFileType(file.getContentType());
            report.setFileName(file.getOriginalFilename());
            report.setFileSize(file.getSize());
            report.setImageUrl("http://localhost:8080/api/reports/image/" + reportId);
            return reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

    // Update report image by Employee ID
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
            report.setFileName(file.getOriginalFilename());
            report.setFileSize(file.getSize());
            report.setImageUrl("http://localhost:8080/api/reports/image/" + report.getReportId());
            return reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

    // Delete report
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
*/

/*package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Report;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.ReportRepository;
import com.pipTracker.Service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final EmployeeRepository employeeRepository;

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png")
        );
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));
    }

    @Override
    public List<Report> getReportsByEmployeeId(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        return reportRepository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    public byte[] getEmployeeImage(Long employeeId, String name) {
        List<Report> reports = reportRepository.findByEmployee_EmployeeId(employeeId);
        for (Report r : reports) {
            if (r.getFileData() != null) return r.getFileData();
        }
        throw new RuntimeException("No image found for employee " + employeeId);
    }

    @Override
    public Report updateReport(Long reportId, Report updated) {
        Report existing = getReportById(reportId);
        existing.setCreatedBy(updated.getCreatedBy());
        existing.setReportType(updated.getReportType());
        return reportRepository.save(existing);
    }

    @Override
    public Report updateReportImage(Long reportId, MultipartFile file) {
        Report report = getReportById(reportId);
        try {
            report.setFileData(file.getBytes());
            report.setFileType(file.getContentType());
            report.setFileSize(file.getSize());
            report.setPhotoUrl(generateReportFileUrl(reportId));
            return reportRepository.save(report);
        } catch (IOException e) {
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
            report.setFileData(file.getBytes());
            report.setFileType(file.getContentType());
            report.setFileSize(file.getSize());
            report.setPhotoUrl(generateReportFileUrl(report.getReportId()));
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException("Report not found with id: " + reportId);
        }
        reportRepository.deleteById(reportId);
        return true;
    }

    // Utility method to build accessible image URL
    @Override
    public String generateReportFileUrl(Long reportId) {
        return "http://localhost:8080/api/reports/get-image?reportId=" + reportId;
    }
}
*/
package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Report;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.ReportRepository;
import com.pipTracker.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final EmployeeRepository employeeRepository;

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png")
        );
    }


    @Override
    public Report createReport(Report report, Long employeeId, MultipartFile file) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        try {
            if (file != null && !file.isEmpty()) {
                if (!isValidImageType(file.getContentType())) {
                    throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG are allowed.");
                }
                report.setFileData(file.getBytes());
                report.setFileName(file.getOriginalFilename());
                report.setFileType(file.getContentType());
                report.setFileSize(file.getSize());
                report.setPhotoUrl(generateReportFileUrl(report.getReportId()));
            }

            report.setEmployee(employee);
            return reportRepository.save(report);

        } catch (IOException e) {
            throw new RuntimeException("Error while uploading report: " + e.getMessage());
        }
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));
    }

    @Override
    public List<Report> getReportsByEmployeeId(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        return reportRepository.findByEmployee_EmployeeId(employeeId);
    }

//    @Override
//    public byte[] getEmployeeImage(Long employeeId, String name) {
//        List<Report> reports = reportRepository.findByEmployee_EmployeeId(employeeId);
//        for (Report r : reports) {
//            if (r.getFileData() != null) return r.getFileData();
//        }
//        throw new RuntimeException("No image found for employee " + employeeId);
//    }
//@Override
//public byte[] getEmployeeImage(Long employeeId, String name) {
//    List<Report> reports = reportRepository.findByEmployee_EmployeeId(employeeId);
//    for (Report r : reports) {
//        if (r.getFileData() != null) return r.getFileData();
//    }
//    throw new RuntimeException("No image found for employee " + employeeId);
//}

    @Override
    public byte[] getEmployeeImage(Long employeeId, String name) {
        List<Report> reports = reportRepository.findByEmployee_EmployeeId(employeeId);
        for (Report r : reports) {
            if (!r.getEmployee().getName().equals(name)) {
                throw new RuntimeException("EmployeeId and Name do not match");
            }
            if (r.getFileData() != null) return r.getFileData();
        }
        throw new RuntimeException("No image found for employee " + employeeId);
    }


    @Override
    public Report updateReport(Long reportId, Report updated) {
        Report existing = getReportById(reportId);
        existing.setCreatedBy(updated.getCreatedBy());
        existing.setReportType(updated.getReportType());
        return reportRepository.save(existing);
    }

    @Override
    public Report updateReportImage(Long reportId, MultipartFile file) {
        Report report = getReportById(reportId);
        try {
            report.setFileData(file.getBytes());
            report.setFileType(file.getContentType());
            report.setFileSize(file.getSize());
            report.setPhotoUrl(generateReportFileUrl(reportId));
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }catch (ReportNotFoundException e) {
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
            report.setFileData(file.getBytes());
            report.setFileType(file.getContentType());
            report.setFileSize(file.getSize());
            report.setPhotoUrl(generateReportFileUrl(report.getReportId()));
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException("Report not found with id: " + reportId);
        }
        reportRepository.deleteById(reportId);
        return true;
    }



    @Override
    public String generateReportFileUrl(Long reportId) {
        return "http://localhost:8080/api/reports/get-image?reportId=" + reportId;
    }
}
