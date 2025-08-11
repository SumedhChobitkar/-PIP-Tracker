package com.pipTracker.Controller;

import com.pipTracker.Entity.FeedBack;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/feedback")
public class FeedBackController
{
    @Autowired
    private FeedBackService feedbackservice;

    @PostMapping("/create")
    public ResponseEntity<?> createFeedback(@RequestBody FeedBack feedback)
    {
        try {
            FeedBack fb = feedbackservice.createFeedback(feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body("FeedBack Added ");
        }
        catch(FeedBackNotFoundException e)
        {
            return ResponseEntity.internalServerError().body("Data Adding Issue");
        }
    }
    @PostMapping("/add/{employeeId}")
    public ResponseEntity<?> addFeedbackToEmployee(@PathVariable Long employeeId, @RequestBody FeedBack feedback)
    {
        try {
            FeedBack saved = feedbackservice.addFeedbackToEmployee(employeeId, feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }
        catch (FeedBackNotFoundException e)
        {
            return ResponseEntity.internalServerError().body("Data Adding Issue");
        }
    }
    @GetMapping("/getall")
    public ResponseEntity<?> getAllFeedback()
    {
        try {
            List<FeedBack> getfb = feedbackservice.getAllFeedback();
            return ResponseEntity.status(HttpStatus.FOUND).body(getfb);
        }
        catch(FeedBackNotFoundException e)
        {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long id,@RequestBody FeedBack fb)
    {
        try {
            FeedBack upfb = feedbackservice.updateFeedback(id, fb);
            return ResponseEntity.ok(upfb);
        }
        catch (FeedBackNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found with ID: " + id);
        }
    }

    @GetMapping("/get/{employeeId}")
    public ResponseEntity<?> getFeedbackByEmployeeId(@PathVariable Long employeeId)
    {
        try {
            return ResponseEntity.ok(feedbackservice.getFeedbackByEmployeeId(employeeId));
        }
        catch (FeedBackNotFoundException e)
        {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<?> updateFeedbackByEmployeeId(@PathVariable Long employeeId, @RequestBody FeedBack feedback)
    {
        try {
            FeedBack updated = feedbackservice.updateFeedbackByEmployeeId(employeeId, feedback);
            return ResponseEntity.ok(updated);
        }
        catch (FeedBackNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found with ID: " + employeeId);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id)
    {
        try {
            feedbackservice.deleteFeedback(id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Successfully");
        }
        catch (FeedBackNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Please provide valid id");
        }
    }
    @DeleteMapping("/delete/{employeeId}/{feedbackId}")
    public ResponseEntity<String> deleteFeedbackByEmployeeId(@PathVariable Long employeeId, @PathVariable Long feedbackId)
    {
        try {
            feedbackservice.deleteFeedbackByEmployeeId(employeeId, feedbackId);
            return ResponseEntity.ok("Feedback deleted successfully for employee ID " + employeeId);
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("Please provide valid id");
        }
    }

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


