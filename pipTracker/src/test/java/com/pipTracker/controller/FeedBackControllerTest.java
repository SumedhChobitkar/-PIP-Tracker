package com.pipTracker.Controller;

import com.pipTracker.Controller.FeedBackController;
import com.pipTracker.Entity.FeedBack;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Service.FeedBackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedBackControllerTest {

    @InjectMocks
    private FeedBackController feedBackController;

    @Mock
    private FeedBackService feedbackService;

    private FeedBack feedback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        feedback = new FeedBack();
        feedback.setFeedbackId(1L);
        feedback.setRating(8);
    }

    @Test
    void testCreateFeedback_Success() {
        when(feedbackService.createFeedback(feedback)).thenReturn(feedback);

        ResponseEntity<?> response = feedBackController.createFeedback(feedback);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("FeedBack Added ", response.getBody());
        verify(feedbackService, times(1)).createFeedback(feedback);
    }

    @Test
    void testCreateFeedback_Error() {
        when(feedbackService.createFeedback(feedback)).thenThrow(new FeedBackNotFoundException("Error"));

        ResponseEntity<?> response = feedBackController.createFeedback(feedback);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Data Adding Issue", response.getBody());
    }

    @Test
    void testAddFeedbackToEmployee_Success() {
        when(feedbackService.addFeedbackToEmployee(1L, feedback)).thenReturn(feedback);

        ResponseEntity<?> response = feedBackController.addFeedbackToEmployee(1L, feedback);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(feedback, response.getBody());
    }

    @Test
    void testAddFeedbackToEmployee_ValidationError() {
        when(feedbackService.addFeedbackToEmployee(1L, feedback))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<?> response = feedBackController.addFeedbackToEmployee(1L, feedback);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Validations IssuesInvalid input", response.getBody());
    }

    @Test
    void testGetAllFeedback_Success() {
        List<FeedBack> feedbackList = Arrays.asList(feedback);
        when(feedbackService.getAllFeedback()).thenReturn(feedbackList);

        ResponseEntity<?> response = feedBackController.getAllFeedback();

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(feedbackList, response.getBody());
    }

    @Test
    void testGetAllFeedback_Error() {
        when(feedbackService.getAllFeedback()).thenThrow(new FeedBackNotFoundException("Error"));

        ResponseEntity<?> response = feedBackController.getAllFeedback();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Something went wrong", response.getBody());
    }

    @Test
    void testUpdateFeedback_Success() {
        when(feedbackService.updateFeedback(1L, feedback)).thenReturn(feedback);

        ResponseEntity<?> response = feedBackController.updateFeedback(1L, feedback);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(feedback, response.getBody());
    }

    @Test
    void testUpdateFeedback_NotFound() {
        when(feedbackService.updateFeedback(1L, feedback))
                .thenThrow(new FeedBackNotFoundException("Not found"));

        ResponseEntity<?> response = feedBackController.updateFeedback(1L, feedback);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Feedback not found with ID: 1", response.getBody());
    }

    @Test
    void testDeleteFeedback_Success() {
        doNothing().when(feedbackService).deleteFeedback(1L);

        ResponseEntity<?> response = feedBackController.deleteFeedback(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deleted Successfully", response.getBody());
    }

    @Test
    void testDeleteFeedback_BadRequest() {
        doThrow(new FeedBackNotFoundException("Invalid id"))
                .when(feedbackService).deleteFeedback(99L);

        ResponseEntity<?> response = feedBackController.deleteFeedback(99L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Please provide valid id", response.getBody());
    }
}
