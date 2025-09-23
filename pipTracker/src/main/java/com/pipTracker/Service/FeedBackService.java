package com.pipTracker.Service;

import com.pipTracker.Entity.FeedBack;

import java.util.List;

public interface FeedBackService
{
    FeedBack createFeedback(FeedBack feedback);
    List<FeedBack> getAllFeedback();
    FeedBack updateFeedback(Long id,FeedBack fb);
    List<FeedBack> getFeedbackByEmployeeId(Long employeeId);
    FeedBack updateFeedbackByEmployeeId(Long employeeId, FeedBack feedback);
    void deleteFeedback(Long id);
    void deleteFeedbackByEmployeeId(Long employeeId, Long feedbackId);
    FeedBack addFeedbackToEmployee(Long employeeId, FeedBack feedback);
    void deleteAllFeedbackByEmployeeId(Long employeeId);
}
