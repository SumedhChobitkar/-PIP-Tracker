/*package com.pipTracker.ServiceImpl;

import com.pipTracker.CommonUtil.Validation;
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

import java.time.LocalDate;
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
        Validation.validatePerformanceReview(review, false);

        Employee reviewer = getLoggedInEmployee();

        review.setReviewer(reviewer);
        if (review.getOverallRating() == null) review.setOverallRating(0.0);

        PerformanceReview saved = repository.save(review);
        log.info("Saved PerformanceReview with ID: {}", saved.getReviewId());

        createAuditLog(saved, reviewer);
        sendNotification(saved);
        sendEmail(saved, reviewer);

        return saved;
    }

    @Override
    public PerformanceReview save(PerformanceReview review) {
        Validation.validatePerformanceReview(review, false);
        if (review.getOverallRating() == null) review.setOverallRating(0.0);
        return repository.save(review);
    }

    @Override
    public PerformanceReview saveWithReviewer(PerformanceReview review, Long reviewerId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReview getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Review not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> getAll() {
        return repository.findAll();
    }

    @Override
    public PerformanceReview update(Long id, PerformanceReview incoming) {
        Validation.validatePerformanceReview(incoming, true);

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

            Validation.validatePerformanceReview(review, true);

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

    @Override
    public PerformanceReview saveWithReviewerAuto(PerformanceReview review, Long employeeId) {
        return null;
    }

    // === Helper Methods ===
    private Employee getLoggedInEmployee() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        return employeeRepository.findById(user.getEmployee().getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Reviewer Employee not found"));
    }

    private void createAuditLog(PerformanceReview review, Employee reviewer) {
        AuditLog audit = new AuditLog();
        audit.setUserId(review.getEmployee().getEmployeeId());
        audit.setEntityname(EntityName.REVIEW);
        audit.setEntityId(review.getReviewId());
        audit.setAction(Action.CREATE);
        audit.setTimestamp(LocalDateTime.now());
        audit.setRemarks("Review created by logged-in user " + reviewer.getEmployeeId());
        auditLogService.createAuditLogPerformanceReview(audit);
    }

    private void sendNotification(PerformanceReview review) {
        Notification notification = new Notification();
        notification.setUserId(review.getEmployee().getEmployeeId());
        notification.setTitle("Review Submitted");
        notification.setMessage("A new review has been submitted for you.");
        notification.setType("ALERT");
        notification.setTimestamp(LocalDateTime.now());
        notificationService.createNotification(notification);
    }

    private void sendEmail(PerformanceReview review, Employee reviewer) {
        String toEmail = reviewer.getEmail();
        if (toEmail != null) {
            String subject = "New Review Notification";
            String body = "Dear " + reviewer.getName() + ",\n\n" +
                    "You have submitted a new review for " + review.getEmployee().getName() + ".\n" +
                    "Please check it in the system.\n\nBest Regards,\nHR Team\n\n(This is an auto-generated mail)";
            emailSenderService.sendEmail(toEmail, subject, body);
        }
    }
}
 */
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
import org.springframework.http.MediaType;
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

    //Create Review (auto reviewer from logged-in user)
    @Override
    public PerformanceReview saveWithReviewerAuto(PerformanceReview review) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
            Employee reviewer = employeeRepository.findById(user.getEmployee().getEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Reviewer Employee not found"));

            review.setReviewer(reviewer);
            if (review.getOverallRating() == null) review.setOverallRating(0.0);

            PerformanceReview saved = repository.save(review);

            //Audit Log
            AuditLog audit = new AuditLog();
            audit.setUserId(saved.getEmployee().getEmployeeId());
            audit.setEntityname(EntityName.REVIEW);
            audit.setEntityId(saved.getReviewId());
            audit.setAction(Action.CREATE);
            audit.setTimestamp(LocalDateTime.now());
            audit.setRemarks("Review created by " + reviewer.getEmployeeId());
            auditLogService.createAuditLogPerformanceReview(audit);

            //Notification
            Notification notification = new Notification();
            notification.setUserId(saved.getEmployee().getEmployeeId());
            notification.setTitle("Review Submitted");
            notification.setMessage("A new review has been submitted for you.");
            notification.setType("ALERT");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.createNotification(notification);

            return saved;

        } catch (Exception e) {
            log.error("Error while saving PerformanceReview: {}", e.getMessage());
            throw e;
        }
    }

    //Added missing method to fix compile error
    @Override
    public PerformanceReview saveWithReviewer(PerformanceReview review, Long reviewerId) {
        // Optional usage: reviewerId explicitly passed
        Employee reviewer = employeeRepository.findById(reviewerId)
                .orElseThrow(() -> new EmployeeNotFoundException("Reviewer not found with id: " + reviewerId));
        review.setReviewer(reviewer);
        return repository.save(review);
    }

    @Override
    public PerformanceReview save(PerformanceReview review) {
        if (review.getOverallRating() == null) review.setOverallRating(0.0);
        return repository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReview getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Review not found with id: " + id));
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
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new PerformanceReviewNotFoundException("Review not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<PerformanceReview> getByEmployeeId(Long employeeId) {
        return repository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    public List<PerformanceReview> getByReviewerId(Long reviewerId) {
        return repository.findByReviewer_EmployeeId(reviewerId);
    }

    //PDF Upload
    @Override
    public PerformanceReview uploadFile(Long id, MultipartFile file) {
        try {
            PerformanceReview review = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Performance Review not found with id: " + id));

            if (file == null || file.isEmpty()) {
                throw new RuntimeException("No file selected for upload.");
            }
            if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {
                throw new RuntimeException("Only PDF files are allowed.");
            }

            review.setFileName(file.getOriginalFilename());
            review.setFileType(file.getContentType());
            review.setFileData(file.getBytes());

            return repository.save(review);
        } catch (Exception e) {
            throw new RuntimeException("Error while uploading PDF: " + e.getMessage());
        }
    }

    //PDF Download
    @Override
    public byte[] downloadFile(Long id) {
        PerformanceReview review = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (review.getFileData() == null || review.getFileData().length == 0) {
            throw new RuntimeException("No PDF uploaded for this review. Please upload a file first.");
        }

        return review.getFileData();
    }
}

