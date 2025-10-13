package com.pipTracker.controller;

import com.pipTracker.Controller.SkillGapAnalysisController;
import com.pipTracker.Entity.SkillGapAnalysis;
import com.pipTracker.Exception.SkillGapAnalysisNotfoundException;
import com.pipTracker.Service.SkillGapAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillGapsControllerUnitTest {

    @Mock
    private SkillGapAnalysisService skillGapService;

    @InjectMocks
    private SkillGapAnalysisController skillGapController;

    @Test
    void testAddSkillGap_Success() throws Exception {
        SkillGapAnalysis skillGap = new SkillGapAnalysis();

        when(skillGapService.addSkillGap(1L, skillGap)).thenReturn(skillGap);

        ResponseEntity<?> response = skillGapController.addSkillGap(1L, skillGap);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("SkillGap Added", response.getBody());
        verify(skillGapService, times(1)).addSkillGap(1L, skillGap);
    }

    @Test
    void testGetSkillGapsByEmployee_WithData() {
        SkillGapAnalysis skillGap = new SkillGapAnalysis();

        when(skillGapService.getSkillGapsByEmployee(1L)).thenReturn(Arrays.asList(skillGap));

        ResponseEntity<?> response = skillGapController.getSkillGapsByEmployee(1L);

        assertEquals(200, response.getStatusCodeValue());
        List<?> list = (List<?>) response.getBody();
        assertEquals(1, list.size());
        verify(skillGapService, times(1)).getSkillGapsByEmployee(1L);
    }

    @Test
    void testGetSkillGapsByEmployee_NoData() {
        when(skillGapService.getSkillGapsByEmployee(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = skillGapController.getSkillGapsByEmployee(1L);

        assertEquals(202, response.getStatusCodeValue());
        verify(skillGapService, times(1)).getSkillGapsByEmployee(1L);
    }

    @Test
    void testUpdateSkillGap_Success() throws Exception {
        SkillGapAnalysis skillGap = new SkillGapAnalysis();
        when(skillGapService.updateSkillGap(1L, skillGap)).thenReturn(skillGap);

        ResponseEntity<?> response = skillGapController.updateSkillGap(1L, skillGap);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(skillGapService, times(1)).updateSkillGap(1L, skillGap);
    }

    @Test
    void testUpdateSkillGap_NotFound() throws Exception {
        SkillGapAnalysis skillGap = new SkillGapAnalysis();
        when(skillGapService.updateSkillGap(1L, skillGap)).thenThrow(SkillGapAnalysisNotfoundException.class);

        ResponseEntity<?> response = skillGapController.updateSkillGap(1L, skillGap);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteAllSkillGapByEmployee() {
        doNothing().when(skillGapService).deleteAllSkillGapByEmployee(1L);

        ResponseEntity<String> response = skillGapController.deleteAllSkillGapByEmployee(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(skillGapService, times(1)).deleteAllSkillGapByEmployee(1L);
    }

    @Test
    void testDeleteSkillGapByEmployee() {
        doNothing().when(skillGapService).deleteSkillGapByEmployee(1L, 1L);

        ResponseEntity<String> response = skillGapController.deleteSkillGapByEmployee(1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(skillGapService, times(1)).deleteSkillGapByEmployee(1L, 1L);
    }

    @Test
    void testAddSkillGap_Failure() throws Exception {
        SkillGapAnalysis skillGap = new SkillGapAnalysis();
        when(skillGapService.addSkillGap(1L, skillGap))
                .thenThrow(new RuntimeException("Failed to add skill gap"));

        ResponseEntity<?> response = skillGapController.addSkillGap(1L, skillGap);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue()); // Assuming you return 500
        assertEquals("Failed to add skill gap", response.getBody()); // Or your error message
        verify(skillGapService, times(1)).addSkillGap(1L, skillGap);
    }

    @Test
    void testGetSkillGapsByEmployee_Exception() {
        when(skillGapService.getSkillGapsByEmployee(1L))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> skillGapController.getSkillGapsByEmployee(1L));

        assertEquals("Database error", ex.getMessage());
        verify(skillGapService, times(1)).getSkillGapsByEmployee(1L);
    }

    @Test
    void testUpdateSkillGap_Exception() {
        SkillGapAnalysis skillGap = new SkillGapAnalysis();

        when(skillGapService.updateSkillGap(1L, skillGap))
                .thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<?> response = skillGapController.updateSkillGap(1L, skillGap);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Update failed", response.getBody());

        verify(skillGapService, times(1)).updateSkillGap(1L, skillGap);
    }


    @Test
    void testDeleteAllSkillGapByEmployee_Exception() {
        doThrow(new RuntimeException("Delete all failed"))
                .when(skillGapService).deleteAllSkillGapByEmployee(1L);

        ResponseEntity<String> response = skillGapController.deleteAllSkillGapByEmployee(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to delete analysis for employee ID: 1", response.getBody());

        verify(skillGapService, times(1)).deleteAllSkillGapByEmployee(1L);
    }

    @Test
    void testDeleteSkillGapByEmployee_Exception() {
        doThrow(new RuntimeException("Delete skill gap failed"))
                .when(skillGapService).deleteSkillGapByEmployee(1L, 1L);

        ResponseEntity<String> response = skillGapController.deleteSkillGapByEmployee(1L, 1L);

        assertNotNull(response);

        assertEquals(400, response.getStatusCodeValue());

        assertEquals("Failed to delete analysis for employee ID:1AnalysisId:1", response.getBody());

        verify(skillGapService, times(1)).deleteSkillGapByEmployee(1L, 1L);
    }

}

