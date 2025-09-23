package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PerformanceReviewNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PerformanceReviewRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.Service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository repository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService auditLogService;
    private final Notificationservice notificationService;
    private final EmailSenderService emailSenderService;

    // === Save Review with reviewer auto-set from logged-in user ===
    @Override
    public PerformanceReview saveWithReviewerAuto(PerformanceReview review) {
        try {
            // 1. Get logged-in user email
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // 2. Get User and Employee
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
            Employee reviewer = employeeRepository.findById(user.getEmployee().getEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Reviewer Employee not found"));

            // 3. Set reviewer
            review.setReviewer(reviewer);

            if (review.getOverallRating() == null) review.setOverallRating(0.0);

            PerformanceReview saved = repository.save(review);
            log.info("Saved PerformanceReview with ID: {}", saved.getReviewId());

            // === Audit Log ===
            AuditLog audit = new AuditLog();
            audit.setUserId(saved.getEmployee().getEmployeeId());
            audit.setEntityname(EntityName.REVIEW);
            audit.setEntityId(saved.getReviewId());
            audit.setAction(Action.CREATE);
            audit.setTimestamp(LocalDateTime.now());
            audit.setRemarks("Review created by logged-in user " + reviewer.getEmployeeId());
            auditLogService.createAuditLogPerformanceReview(audit);

            // === Notification ===
            Notification notification = new Notification();
            notification.setUserId(saved.getEmployee().getEmployeeId());
            notification.setTitle("Review Submitted");
            notification.setMessage("A new review has been submitted for you.");
            notification.setType("ALERT");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.createNotification(notification);

            // === Email to Reviewer ===
            String toEmail = reviewer.getEmail();
            if (toEmail != null) {
                String subject = "New Review Notification";
                String body = "Dear " + reviewer.getName() + ",\n\n" +
                        "You have submitted a new review for " + saved.getEmployee().getName() + ".\n" +
                        "Please check it in the system.\n\nBest Regards,\nHR Team\n\n(This is an auto-generated mail)";
                emailSenderService.sendEmail(toEmail, subject, body);
            }

            return saved;
        } catch (Exception e) {
            log.error("Error while saving PerformanceReview: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PerformanceReview save(PerformanceReview review) {
        if (review.getOverallRating() == null) review.setOverallRating(0.0);
        return repository.save(review);
    }

    @Override
    public PerformanceReview saveWithReviewer(PerformanceReview review, Long reviewerId) {
        return null; // Deprecated: now using saveWithReviewerAuto
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReview getById(Long id) {
        PerformanceReview review = repository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Review not found with id: " + id));

        // Auto-set reviewer if null
        if (review.getReviewer() == null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
            Employee reviewer = employeeRepository.findById(user.getEmployee().getEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Reviewer Employee not found"));
            review.setReviewer(reviewer);
            repository.save(review);
        }

        return review;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getAll() {
        return repository.findAll();
    }

    @Override
    public PerformanceReview update(Long id, PerformanceReview incoming) {
        PerformanceReview existing = getById(id);
        existing.setReviewPeriod(incoming.getReviewPeriod());
        existing.setReviewDate(incoming.getReviewDate());
        existing.setScores(incoming.getScores());
        existing.setOverallRating(incoming.getOverallRating());
        existing.setComments(incoming.getComments());
        existing.setReviewType(incoming.getReviewType());
        existing.setFileName(incoming.getFileName());
        existing.setFileType(incoming.getFileType());
        existing.setFileData(incoming.getFileData());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new PerformanceReviewNotFoundException("Performance Review not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getByEmployeeId(Long employeeId) {
        return repository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getByReviewerId(Long reviewerId) {
        return repository.findByReviewer_EmployeeId(reviewerId);
    }

    @Override
    public PerformanceReview uploadFile(Long id, MultipartFile file) {
        try {
            PerformanceReview review = getById(id);
            review.setFileName(file.getOriginalFilename());
            review.setFileType(file.getContentType());
            review.setFileData(file.getBytes());
            return repository.save(review);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    @Override
    public byte[] downloadFile(Long id) {
        PerformanceReview review = getById(id);
        if (review.getFileData() == null) {
            throw new RuntimeException("No file uploaded for this review");
        }
        return review.getFileData();
    }
}
