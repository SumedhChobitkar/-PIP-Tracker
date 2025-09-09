package com.pipTracker.Service;

import com.pipTracker.Entity.Report;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {
    Report createReport(Report report, Long employeeId, MultipartFile file);
    List<Report> getAllReports();
    Report getReportById(Long reportId);
    Report updateReport(Long reportId, Report updated);
    List<Report> getReportsByEmployeeId(Long employeeId);
    byte[] getEmployeeImage(Long employeeId, String name);
    Report updateReportImage(Long reportId, MultipartFile file);
    Report updateImageByEmployeeId(Long employeeId, MultipartFile file);
    boolean deleteReport(Long reportId);


}