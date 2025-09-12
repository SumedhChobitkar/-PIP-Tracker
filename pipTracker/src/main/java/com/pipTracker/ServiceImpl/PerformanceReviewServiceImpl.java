package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PerformanceReviewNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PerformanceReviewRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.Service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository repository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private Notificationservice notificationService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public PerformanceReview save(PerformanceReview review) {
        try {
            if (review.getOverallRating() == null) review.setOverallRating(0.0);
            PerformanceReview saved = repository.save(review);
            log.info("Saved PerformanceReview with ID: {}", saved.getReviewId());
            System.out.println("DEBUG: Saved review for employee " + review.getEmployee());

            AuditLog log1=new AuditLog();
            log1.setUserId(saved.getEmployee().getEmployeeId());
            log1.setEntityname(EntityName.REVIEW);
            log1.setEntityId(saved.getReviewId());
            log1.setAction(Action.CREATE);
            log1.setTimestamp(LocalDateTime.now());
            log1.setRemarks("Review Created");
            auditLogService.createAuditLogPerformanceReview(log1);

            Notification notification = new Notification();
            notification.setUserId(saved.getEmployee().getEmployeeId());
            notification.setTitle("Review Submitted");
            notification.setMessage("New Review Submitted");
            notification.setType("ALERT");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.createNotification(notification);

            if (saved.getReviewer() != null) {
                Optional<Employee> opt = employeeRepository.findById(saved.getReviewer().getEmployeeId());
                if (opt.isPresent()) {
                    Employee toEmployee = opt.get();
                    String toEmail = toEmployee.getEmail();
                    String subject = "New Review Notification";
                    String body = "Dear " + toEmployee.getName()+","+"\n\nI hope this message finds you a well. "+
                            "\nYou have received new Review from User ID: " + saved.getReviewer().getName()+".Please Check It."+"\nIf you have any related queries feel free to reach out us."+"\n\n"
                            +"Best Regards,"+"\n"+"HR Team."+"\n\n\nThis is auto-generated mail.";

                    emailSenderService.sendEmail(toEmail, subject, body);
                }
                else {
                    throw new EmployeeNotFoundException("Id not found of user Id"+saved.getReviewer());
                }
            }
            else {
                throw new NullPointerException("Enter Valid Id");
            }
            return saved;
        } catch (Exception e) {
            log.error("Error while saving PerformanceReview: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReview getById(Long id) {
        try {
            log.info("Fetching PerformanceReview with ID: {}", id);
            return repository.findById(id)
                    .orElseThrow(() -> new PerformanceReviewNotFoundException(
                            "Performance Review not found with id: " + id));
        } catch (PerformanceReviewNotFoundException e) {
            log.warn("PerformanceReview not found for ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching review with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getAll() {
        try {
            log.info("Fetching all PerformanceReviews");
            List<PerformanceReview> list = repository.findAll();
            System.out.println("DEBUG: Total Reviews fetched = " + list.size());
            return list;
        } catch (Exception e) {
            log.error("Error while fetching all PerformanceReviews: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PerformanceReview update(Long id, PerformanceReview incoming) {
        try {
            PerformanceReview existing = getById(id);
            log.info("Updating PerformanceReview with ID: {}", id);

            existing.setEmployee(incoming.getEmployee());
            existing.setReviewer(incoming.getReviewer());
            existing.setReviewPeriod(incoming.getReviewPeriod());
            existing.setReviewDate(incoming.getReviewDate());
            existing.setScores(incoming.getScores());
            existing.setOverallRating(incoming.getOverallRating());
            existing.setComments(incoming.getComments());
            existing.setReviewType(incoming.getReviewType());
            existing.setPdfUrl(incoming.getPdfUrl());

            PerformanceReview updated = repository.save(existing);

            AuditLog log2=new AuditLog();
            log2.setUserId(existing.getEmployee().getEmployeeId());
            log2.setEntityname(EntityName.REVIEW);
            log2.setEntityId(id);
            log2.setAction(Action.UPDATE);
            log2.setTimestamp(LocalDateTime.now());
            log2.setRemarks("REVIEW updated");
            auditLogService.updateAuditlogPerformanceReview(log2);
            System.out.println("DEBUG: Updated review ID " + id);
            return updated;
        } catch (PerformanceReviewNotFoundException e) {
            log.warn("Cannot update - Review not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error while updating PerformanceReview ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                log.warn("Attempted to delete non-existing PerformanceReview ID: {}", id);
                throw new PerformanceReviewNotFoundException("Performance Review not found with id: " + id);
            }
            repository.deleteById(id);

            AuditLog log3 = new AuditLog();
            log3.setUserId(id);
            log3.setEntityname(EntityName.REVIEW);
            log3.setEntityId(id);
            log3.setAction(Action.DELETE);
            log3.setTimestamp(LocalDateTime.now());
            log3.setRemarks("REVIEW Deleted");
            auditLogService.createAuditLogPerformanceReview(log3);
            log.info("Deleted PerformanceReview with ID: {}", id);
            System.out.println("DEBUG: Deleted review with ID " + id);
        } catch (Exception e) {
            log.error("Error while deleting PerformanceReview ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getByEmployeeId(Long employeeId) {
        try {
            log.info("Fetching reviews for Employee ID: {}", employeeId);
            List<PerformanceReview> list = repository.findByEmployee_EmployeeId(employeeId);
            System.out.println("DEBUG: Reviews fetched for Employee " + employeeId + " = " + list.size());
            return list;
        } catch (Exception e) {
            log.error("Error while fetching reviews for employee {}: {}", employeeId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getByReviewerId(Long reviewerId) {
        try {
            log.info("Fetching reviews by Reviewer ID: {}", reviewerId);
            List<PerformanceReview> list = repository.findByReviewer_EmployeeId(reviewerId);
            System.out.println("DEBUG: Reviews fetched by Reviewer " + reviewerId + " = " + list.size());
            return list;
        } catch (Exception e) {
            log.error("Error while fetching reviews by reviewer {}: {}", reviewerId, e.getMessage(), e);
            throw e;
 }
}
}
