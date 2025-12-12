package com.pipTracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pipTracker.Controller.PerformanceReviewController;
import com.pipTracker.Entity.PerformanceReview;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Service.PerformanceReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PerformanceReviewControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PerformanceReviewService service;

    @InjectMocks
    private PerformanceReviewController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(objectMapper),
                        new ByteArrayHttpMessageConverter(), // for file download
                        new StringHttpMessageConverter()      // for text/plain
                )
                .build();
    }

    @Test
    void testCreateReview_success() throws Exception {
        Employee emp = new Employee();
        emp.setEmployeeId(10L);
        Employee reviewer = new Employee();
        reviewer.setEmployeeId(20L);

        PerformanceReview review = new PerformanceReview();
        review.setReviewId(1L);
        review.setEmployee(emp);
        review.setReviewer(reviewer);
        review.setComments("Improve skills");
        review.setReviewPeriod("Q1-2024");
        review.setReviewDate(LocalDate.of(2024,1,1));
        review.setOverallRating(4.5);

        when(service.saveWithReviewerAuto(any(PerformanceReview.class))).thenReturn(review);

        mockMvc.perform(post("/api/performance-reviews/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/performance-reviews/getById/1"))
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.comments").value("Improve skills"))
                .andExpect(jsonPath("$.reviewPeriod").value("Q1-2024"))
                .andExpect(jsonPath("$.overallRating").value(4.5))
                .andExpect(jsonPath("$.employee.employeeId").value(10))
                .andExpect(jsonPath("$.reviewer.employeeId").value(20))
                .andExpect(jsonPath("$.reviewDate").value("2024-01-01"));

        verify(service, times(1)).saveWithReviewerAuto(any(PerformanceReview.class));
    }

    @Test
    void testGetById_success() throws Exception {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(1L);
        review.setComments("Good job");

        when(service.getById(1L)).thenReturn(review);

        mockMvc.perform(get("/api/performance-reviews/getById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.comments").value("Good job"));

        verify(service, times(1)).getById(1L);
    }

    @Test
    void testGetAll_success() throws Exception {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(1L);
        review.setComments("All good");

        when(service.getAll()).thenReturn(List.of(review));

        mockMvc.perform(get("/api/performance-reviews/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(1))
                .andExpect(jsonPath("$[0].comments").value("All good"));

        verify(service, times(1)).getAll();
    }

    @Test
    void testUpdate_success() throws Exception {
        PerformanceReview incoming = new PerformanceReview();
        incoming.setComments("Updated comments");
        incoming.setReviewPeriod("Q2-2024");

        PerformanceReview updated = new PerformanceReview();
        updated.setReviewId(1L);
        updated.setComments("Updated comments");
        updated.setReviewPeriod("Q2-2024");

        when(service.update(eq(1L), any(PerformanceReview.class))).thenReturn(updated);

        mockMvc.perform(put("/api/performance-reviews/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.comments").value("Updated comments"))
                .andExpect(jsonPath("$.reviewPeriod").value("Q2-2024"));

        verify(service, times(1)).update(eq(1L), any(PerformanceReview.class));
    }

    @Test
    void testDelete_success() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/performance-reviews/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted with id 1"));

        verify(service, times(1)).delete(1L);
    }

    @Test
    void testGetByEmployee_success() throws Exception {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(1L);
        when(service.getByEmployeeId(10L)).thenReturn(List.of(review));

        mockMvc.perform(get("/api/performance-reviews/employee/{employeeId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(1));

        verify(service, times(1)).getByEmployeeId(10L);
    }

    @Test
    void testGetByReviewer_success() throws Exception {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(2L);
        when(service.getByReviewerId(20L)).thenReturn(List.of(review));

        mockMvc.perform(get("/api/performance-reviews/reviewer/{reviewerId}", 20L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(2));

        verify(service, times(1)).getByReviewerId(20L);
    }

    @Test
    void testUploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy".getBytes()
        );

        PerformanceReview review = new PerformanceReview();
        review.setReviewId(1L);
        review.setFileName("test.pdf");

        when(service.uploadFile(eq(1L), any())).thenReturn(review);

        mockMvc.perform(multipart("/api/performance-reviews/{id}/upload", 1L).file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("File uploaded successfully")));

        verify(service, times(1)).uploadFile(eq(1L), any());
    }

    @Test
    void testDownloadFile_success() throws Exception {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(1L);
        review.setFileName("test.pdf");
        review.setFileType(MediaType.APPLICATION_PDF_VALUE);

        byte[] fileData = "dummy".getBytes();

        when(service.getById(1L)).thenReturn(review);
        when(service.downloadFile(1L)).thenReturn(fileData);

        mockMvc.perform(get("/api/performance-reviews/{id}/download", 1L))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(fileData));

        verify(service, times(1)).getById(1L);
        verify(service, times(1)).downloadFile(1L);
    }
}
