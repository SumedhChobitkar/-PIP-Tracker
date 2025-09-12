package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.FeedBackRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.FeedBackService;
import com.pipTracker.Service.Notificationservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedBackServiceImpl implements FeedBackService
{
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FeedBackRepository feedbackrepo;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private Notificationservice notificationService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(FeedBackServiceImpl.class);

    public FeedBack createFeedback(FeedBack feedback)
    {
        try
        {
            return feedbackrepo.save(feedback);
        }
        catch (FeedBackNotFoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public FeedBack addFeedbackToEmployee(Long employeeId, FeedBack feedback)
    {
        try {
            Optional<Employee> optional = employeeRepository.findById(employeeId);

            if (optional.isPresent()) {
                Employee employee = optional.get();
                feedback.setEmployee(employee);
                feedback.setCreatedDate(LocalDateTime.now());
                feedback.setFromUserId(employeeId);
                FeedBack fb=feedbackrepo.save(feedback);

                AuditLog log=new AuditLog();
                log.setUserId(employee.getEmployeeId());
                log.setEntityId(fb.getFeedbackId());
                log.setTimestamp(LocalDateTime.now());
                log.setEntityname(EntityName.FEEDBACK);
                log.setAction(Action.CREATE);
                log.setRemarks("FeedBack Created");
                auditLogService.createAuditLogFeedBack(log);

                Notification notification = new Notification();
                notification.setUserId(employee.getEmployeeId());
                notification.setTitle("New Feedback Received");
                notification.setMessage("You have received new feedback from user: " + fb.getFromUserId());
                notification.setType("ALERT");
                notification.setTimestamp(LocalDateTime.now());
                notificationService.createNotification(notification);

                if (fb.getToUserId() != null) {
                    Optional<Employee> Opt = employeeRepository.findById(fb.getToUserId());
                    if (Opt.isPresent()) {
                        Employee toEmployee =Opt.get();
                        Employee fromEmployee=optional.get();
                        String toEmail = toEmployee.getEmail();
                        String subject = "New Feedback Notification";
                        String body = "Dear " + toEmployee.getName()+","+"\n\nI hope this message finds you a well. "+
                                "\nYou have received new feedback from User ID: " + fb.getFromUserId()+".Please Check It."+"\nIf you have any related queries feel free to reach out us."+"\n\n"
                                +"Best Regards,"+"\n"+"HR Team."+"\n\n\nThis is auto-generated mail.";

                        emailSenderService.sendEmail(toEmail, subject, body);
                    }
                    else {
                        throw new EmployeeNotFoundException("Id not found of user Id"+fb.getToUserId());
                    }
                }
                else {
                    throw new NullPointerException("Enter Valid Id");
                }
                return fb;
            } else {
                throw new FeedBackNotFoundException("Employee not found with ID: " + employeeId);
            }
        }
        catch(Exception e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }

    public List<FeedBack> getAllFeedback()
    {
        try
        {
            return feedbackrepo.findAll();
        }
        catch (FeedBackNotFoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }

    public FeedBack updateFeedback(Long id,FeedBack fb)
    {
        try {
            Optional<FeedBack> upfb = feedbackrepo.findById(id);
            if (upfb.isPresent()) {
                FeedBack f = upfb.get();
                f.setFromUserId(fb.getFromUserId());
                f.setToUserId(fb.getToUserId());
                f.setFeedbackType(fb.getFeedbackType());
                f.setComments(fb.getComments());
                f.setRating(fb.getRating());
                f.setIsAnonymous(fb.getIsAnonymous());
                f.setCreatedDate(fb.getCreatedDate());
                return feedbackrepo.save(f);
            }
            else
            {
                return null;
            }
        }
        catch (FeedBackNotFoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }

    public List<FeedBack> getFeedbackByEmployeeId(Long employeeId)
    {
        try {
            return feedbackrepo.findByEmployeeEmployeeId(employeeId);
        }
        catch (FeedBackNotFoundException e)
        {
            logger.info("error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public FeedBack updateFeedbackByEmployeeId(Long employeeId, FeedBack updatedFeedback) {
        try {
            Long feedbackId = updatedFeedback.getFeedbackId();
            if (feedbackId == null) {
                throw new IllegalArgumentException("Feedback ID is required in the request body");
            }
            FeedBack existing = feedbackrepo.findById(feedbackId).orElseThrow();

            if (!existing.getEmployee().getEmployeeId().equals(employeeId)) {
                throw new RuntimeException("Feedback does not belong to the specified employee");
            }

            existing.setFromUserId(employeeId);
            existing.setToUserId(updatedFeedback.getToUserId());
            existing.setFeedbackType(updatedFeedback.getFeedbackType());
            existing.setComments(updatedFeedback.getComments());
            existing.setRating(updatedFeedback.getRating());
            existing.setIsAnonymous(updatedFeedback.getIsAnonymous());
            existing.setCreatedDate(updatedFeedback.getCreatedDate());

            FeedBack saved=feedbackrepo.save(existing);

            AuditLog log = new AuditLog();
            log.setUserId(existing.getFromUserId());
            log.setEntityname(EntityName.FEEDBACK);
            log.setEntityId(updatedFeedback.getFeedbackId());
            log.setAction(Action.UPDATE);
            log.setTimestamp(LocalDateTime.now());
            log.setRemarks("Feedback updated");
            auditLogService.updateAuditlogFeedBack(log);

            return saved;

        } catch (FeedBackNotFoundException ex) {
            logger.info("Feedback not found:"+ ex.getMessage());
            throw ex;
        }
    }

        public void deleteFeedback(Long id)
    {
        try {
            feedbackrepo.deleteById(id);
        }
        catch (FeedBackNotFoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void deleteFeedbackByEmployeeId(Long employeeId, Long feedbackId)
    {
        try {
            FeedBack fb = feedbackrepo.findById(feedbackId)
                    .orElseThrow(() -> new FeedBackNotFoundException("Feedback not found"));

            if (!fb.getEmployee().getEmployeeId().equals(employeeId)) {
                throw new RuntimeException("Feedback does not belong to the specified employee");
            }

            feedbackrepo.delete(fb);
            AuditLog log = new AuditLog();
            log.setUserId(employeeId);
            log.setEntityname(EntityName.FEEDBACK);
            log.setEntityId(feedbackId);
            log.setAction(Action.DELETE);
            log.setTimestamp(LocalDateTime.now());
            log.setRemarks("Feedback deleted");
            auditLogService.createAuditLogFeedBack(log);
        }
        catch (FeedBackNotFoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteAllFeedbackByEmployeeId(Long employeeId) {
        try {
            List<FeedBack> feedbackList = feedbackrepo.findByEmployeeEmployeeId(employeeId);

            if (feedbackList.isEmpty()) {
                throw new FeedBackNotFoundException("No feedback found for employee ID: " + employeeId);
            }

            feedbackrepo.deleteAll(feedbackList);
        }
        catch(FeedBackNotFoundException e)
        {
            logger.info("Error : " + e.getMessage());
            throw e;
        }
    }
}
