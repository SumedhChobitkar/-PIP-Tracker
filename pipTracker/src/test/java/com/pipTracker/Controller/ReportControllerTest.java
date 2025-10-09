package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pipTracker.Entity.Report;
import com.pipTracker.Entity.ReportType;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @Autowired
    private ObjectMapper mapper;
    private Report report;

    /*@BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();

        report = new Report(
                1L,                       // reportId
                1L,                       // createdBy
                ReportType.PERFORMANCE,    // reportType
                LocalDateTime.now(),       // generatedOn
                null,                      // employee (mock or null for test)
                null,                      // file
                null                       // fileType
        );
    }*/
    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        reportController = new ReportController(mapper, reportService); // manual injection
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();

        report = new Report(
                1L,
                1L,
                ReportType.PERFORMANCE,
                LocalDateTime.now(),
                null,
                null,
                null
        );
    }


    // 1. Create Report
   /* @Test
    public void testCreateReport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        String reportJson = mapper.writeValueAsString(report);

        when(reportService.createReport(any(Report.class), anyLong(), any())).thenReturn(report);

        mockMvc.perform(multipart("/api/reports/create/{employeeId}/reports", 1L)
                        .file(file)
                        .param("reportData", reportJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1));
    }*/

    @Test
    public void testCreateReport() throws Exception {
        // Mock file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        // Use mapper with JavaTimeModule (Spring injects same config)
        ObjectMapper mapperWithTimeModule = new ObjectMapper();
        mapperWithTimeModule.registerModule(new JavaTimeModule());
        mapperWithTimeModule.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // JSON string of report
        String reportJson = mapperWithTimeModule.writeValueAsString(report);

        // Mock service response
        when(reportService.createReport(any(Report.class), anyLong(), any())).thenReturn(report);

        // Perform multipart POST
//        mockMvc.perform(multipart("/api/reports/create/{employeeId}/reports", 1L)
//                        .file(file)
//                        .param("reportData", reportJson)
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isOk())
//               // .andExpect(jsonPath("$.reportId").value(1));
//                .andExpect(content().string(org.hamcrest.Matchers.containsString("Report created successfully")));
        mockMvc.perform(multipart("/api/reports/create/{employeeId}/reports", 1L)
                        .file(file)
                        .param("reportData", reportJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1))
                .andExpect(jsonPath("$.createdBy").value(1))
                .andExpect(jsonPath("$.reportType").value("PERFORMANCE"));

    }


    // 2. Get All Reports
    @Test
    public void testGetAllReports() throws Exception {
        when(reportService.getAllReports()).thenReturn(Arrays.asList(report));

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportId").value(1));
    }

    @Test
    public void testGetAllReportsEmpty() throws Exception {
        when(reportService.getAllReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No reports found"));
    }

    // 3. Get Report by ID
    @Test
    public void testGetById() throws Exception {
        when(reportService.getReportById(1L)).thenReturn(report);

        mockMvc.perform(get("/api/reports/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1));
    }

    @Test
    public void testGetByIdNotFound() throws Exception {
        when(reportService.getReportById(2L)).thenThrow(new ReportNotFoundException("Report not found"));

        mockMvc.perform(get("/api/reports/{id}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }

    // 4. Get Reports by Employee ID
    @Test
    public void testGetReportsByEmployeeId() throws Exception {
        when(reportService.getReportsByEmployeeId(1L)).thenReturn(Arrays.asList(report));

        mockMvc.perform(get("/api/reports/employee/{employeeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportId").value(1));
    }

    // 5. Get Employee Image
    @Test
    public void testGetEmployeeImage() throws Exception {
        byte[] imageBytes = "fakeImage".getBytes();
        when(reportService.getEmployeeImage(1L, null)).thenReturn(imageBytes);

        mockMvc.perform(get("/api/reports/get-image").param("employeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageBytes));
    }

    // 6. Update Report
    @Test
    public void testUpdateReport() throws Exception {
        when(reportService.updateReport(eq(1L), any(Report.class))).thenReturn(report);

        mockMvc.perform(put("/api/reports/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1));
    }

    @Test
    public void testUpdateReportNotFound() throws Exception {
        when(reportService.updateReport(eq(2L), any(Report.class)))
                .thenThrow(new ReportNotFoundException("Report not found"));

        mockMvc.perform(put("/api/reports/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(report)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }

    // 7. Update Report Image by Report ID
    @Test
    public void testUpdateReportImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(reportService.updateReportImage(eq(1L), any())).thenReturn(report);

        mockMvc.perform(multipart("/api/reports/update-image/{reportId}", 1L)
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(content().string("Image updated successfully for ReportId: 1"));
    }

    // 8. Update Image by Employee ID
    @Test
    public void testUpdateImageByEmployeeId() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(reportService.updateImageByEmployeeId(eq(1L), any())).thenReturn(report);

        mockMvc.perform(multipart("/api/reports/update-image/employee/{employeeId}", 1L)
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(content().string("Image updated successfully for Employee ID: 1"));
    }

    // 9. Delete Report
    @Test
    public void testDeleteReport() throws Exception {
        when(reportService.deleteReport(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/reports/{reportId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Report deleted successfully"));
    }

    @Test
    public void testDeleteReportNotFound() throws Exception {
        when(reportService.deleteReport(2L)).thenThrow(new ReportNotFoundException("Report not found"));

        mockMvc.perform(delete("/api/reports/{reportId}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }

    @Test
    public void testCreateReport_InvalidJson() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        String invalidJson = "invalid_json";

        mockMvc.perform(multipart("/api/reports/create/{employeeId}/reports", 1L)
                        .file(file)
                        .param("reportData", invalidJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error")));
    }

    @Test
    public void testGetById_InvalidId() throws Exception {
        when(reportService.getReportById(999L)).thenThrow(new ReportNotFoundException("Report not found"));

        mockMvc.perform(get("/api/reports/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }


    @Test
    public void testGetReportsByEmployeeId_NoReports() throws Exception {
        when(reportService.getReportsByEmployeeId(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reports/employee/{employeeId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    public void testGetEmployeeImage_NotFound() throws Exception {
        when(reportService.getEmployeeImage(999L, null))
                .thenThrow(new RuntimeException("Image not found"));

        mockMvc.perform(get("/api/reports/get-image").param("employeeId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Image not found"));
    }


    @Test
    public void testUpdateReport_NotFound() throws Exception {
        when(reportService.updateReport(eq(999L), any(Report.class)))
                .thenThrow(new ReportNotFoundException("Report not found"));

        mockMvc.perform(put("/api/reports/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(report)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }



    @Test
    public void testUpdateReportImage_NotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(reportService.updateReportImage(eq(999L), any()))
                .thenThrow(new RuntimeException("Report not found"));

        mockMvc.perform(multipart("/api/reports/update-image/{reportId}", 999L)
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }


    @Test
    public void testUpdateImageByEmployeeId_Error() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(reportService.updateImageByEmployeeId(eq(999L), any()))
                .thenThrow(new RuntimeException("Failed to update image"));

        mockMvc.perform(multipart("/api/reports/update-image/employee/{employeeId}", 999L)
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error")));
    }


    @Test
    public void testDeleteReport_NotFound() throws Exception {
        when(reportService.deleteReport(999L)).thenThrow(new ReportNotFoundException("Report not found"));

        mockMvc.perform(delete("/api/reports/{reportId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Report not found"));
    }


}
