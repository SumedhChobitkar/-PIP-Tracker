package com.pipTracker.Controller;

import com.pipTracker.Entity.FeedBack;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Service.FeedBackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Feedback APIs",
        description = "Operations related to Employee Feedback management"
)
@RestController
@CrossOrigin("*")
@RequestMapping("/api/feedback")
public class FeedBackController {

    @Autowired
    private FeedBackService feedbackservice;

    @Operation(
            summary = "Create Feedback",
            description = "Creates a new feedback entry.\n\n" +
                    "Eg: POST http://localhost:8080/api/feedback/create"
    )
    @ApiResponse(responseCode = "201", description = "Feedback created successfully")
    @ApiResponse(responseCode = "500", description = "Error while creating feedback")
    @PostMapping("/create")
    public ResponseEntity<?> createFeedback(@RequestBody FeedBack feedback) {
        try {
            FeedBack fb = feedbackservice.createFeedback(feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body("FeedBack Added ");
        } catch (FeedBackNotFoundException e) {
            return ResponseEntity.internalServerError().body("Data Adding Issue");
        }
    }

    @Operation(
            summary = "Add Feedback to Employee",
            description = "Adds feedback for a specific employee by their ID.\n\n" +
                    "Eg: POST http://localhost:8080/api/feedback/add/{employeeId}"
    )
    @ApiResponse(responseCode = "201", description = "Feedback added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid employee ID or input")
    @PostMapping("/add/{employeeId}")
    public ResponseEntity<?> addFeedbackToEmployee(@PathVariable Long employeeId, @RequestBody FeedBack feedback) {
        try {
            FeedBack saved = feedbackservice.addFeedbackToEmployee(employeeId, feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (FeedBackNotFoundException e) {
            return ResponseEntity.internalServerError().body("Data Adding Issue");
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validations Issues"+e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Id Not Found or enter Valid Id");
        }
    }

    @Operation(
            summary = "Get All Feedback",
            description = "Fetches all feedback entries.\n\n" +
                    "Eg: GET http://localhost:8080/api/feedback/getall"
    )
    @ApiResponse(responseCode = "200", description = "Feedback fetched successfully")
    @ApiResponse(responseCode = "500", description = "Error fetching feedback")
    @GetMapping("/getall")
    public ResponseEntity<?> getAllFeedback() {
        try {
            List<FeedBack> getfb = feedbackservice.getAllFeedback();
            return ResponseEntity.status(HttpStatus.FOUND).body(getfb);
        } catch (FeedBackNotFoundException e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @Operation(
            summary = "Update Feedback by ID",
            description = "Updates a feedback entry by its ID.\n\n" +
                    "Eg: PUT http://localhost:8080/api/feedback/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Feedback updated successfully")
    @ApiResponse(responseCode = "404", description = "Feedback not found")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long id, @RequestBody FeedBack fb) {
        try {
            FeedBack upfb = feedbackservice.updateFeedback(id, fb);
            return ResponseEntity.ok(upfb);
        } catch (FeedBackNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found with ID: " + id);
        }
    }

    @Operation(
            summary = "Get Feedback by Employee ID",
            description = "Fetches feedback entries for a specific employee.\n\n" +
                    "Eg: GET http://localhost:8080/api/feedback/get/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Feedback fetched successfully")
    @ApiResponse(responseCode = "500", description = "Error fetching feedback")
    @GetMapping("/get/{employeeId}")
    public ResponseEntity<?> getFeedbackByEmployeeId(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(feedbackservice.getFeedbackByEmployeeId(employeeId));
        } catch (FeedBackNotFoundException e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @Operation(
            summary = "Update Feedback by Employee ID",
            description = "Updates feedback of a specific employee by employee ID.\n\n" +
                    "Eg: PUT http://localhost:8080/api/feedback/update/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Feedback updated successfully")
    @ApiResponse(responseCode = "404", description = "Feedback not found for employee ID")
    @PutMapping("/update/{employeeId}")
    public ResponseEntity<?> updateFeedbackByEmployeeId(@PathVariable Long employeeId, @RequestBody FeedBack feedback) {
        try {
            FeedBack updated = feedbackservice.updateFeedbackByEmployeeId(employeeId, feedback);
            return ResponseEntity.ok(updated);
        } catch (FeedBackNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found with ID: " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Delete Feedback by ID",
            description = "Deletes a feedback entry by its ID.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/feedback/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Feedback deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid feedback ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackservice.deleteFeedback(id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Successfully");
        } catch (FeedBackNotFoundException e) {
            return ResponseEntity.badRequest().body("Please provide valid id");
        }
    }

    @Operation(
            summary = "Delete Feedback by Employee ID and Feedback ID",
            description = "Deletes a specific feedback entry for an employee.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/feedback/delete/{employeeId}/{feedbackId}"
    )
    @ApiResponse(responseCode = "200", description = "Feedback deleted successfully for employee")
    @ApiResponse(responseCode = "400", description = "Invalid employeeId or feedbackId")
    @DeleteMapping("/delete/{employeeId}/{feedbackId}")
    public ResponseEntity<String> deleteFeedbackByEmployeeId(@PathVariable Long employeeId, @PathVariable Long feedbackId) {
        try {
            feedbackservice.deleteFeedbackByEmployeeId(employeeId, feedbackId);
            return ResponseEntity.ok("Feedback deleted successfully for employee ID " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Please provide valid id");
        }
    }

    @Operation(
            summary = "Delete All Feedback by Employee ID",
            description = "Deletes all feedback entries for a given employee.\n\n" +
                    "Eg: DELETE http://localhost:8080/api/feedback/delete/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "All feedback deleted successfully")
    @ApiResponse(responseCode = "400", description = "Failed to delete feedbacks for employee ID")
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<String> deleteAllFeedbackByEmployeeId(@PathVariable Long employeeId) {
        try {
            feedbackservice.deleteAllFeedbackByEmployeeId(employeeId);
            return ResponseEntity.ok("All feedback deleted for employee ID " + employeeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete feedbacks for employee ID: " + employeeId);
        }
    }
}
