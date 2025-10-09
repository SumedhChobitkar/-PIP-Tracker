/*
package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PipRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.Service.PipService;
import com.pipTracker.Validations.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PipServiceImpl implements PipService {

    @Autowired
    private PipRepository pipRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private Notificationservice notificationService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public Pip createPip(Pip pip) {
        try {
            Pip p=pipRepository.save(pip);
            AuditLog log=new AuditLog();
            log.setUserId(p.getPipId());
            log.setEntityId(p.getPipId());
            log.setTimestamp(LocalDateTime.now());
            log.setEntityname(EntityName.PIP);
            log.setAction(Action.CREATE);
            log.setRemarks("PIP Created");
            auditLogService.createAuditLogPip(log);

            Notification notification = new Notification();
            notification.setUserId(p.getEmployee().getEmployeeId());
            notification.setTitle("PIP Stared");
            notification.setMessage("New PIP Started");
            notification.setType("ALERT");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.createNotification(notification);

            if (p.getReviewerId() != null) {
                Optional<Employee> Opt = employeeRepository.findById(p.getReviewerId());
                if (Opt.isPresent()) {
                    Employee toEmployee =Opt.get();
                    String toEmail = toEmployee.getEmail();
                    String subject = "New PIP Notification";
                    String body = "Dear " + toEmployee.getName()+","+"\n\nI hope this message finds you a well. "+
                            "\nYou have received new PIP from User ID: " + pip.getReviewerId()+".Please Check It."+"\nIf you have any related queries feel free to reach out us."+"\n\n"
                            +"Best Regards,"+"\n "+"HR Team"+"\n\n\nThis is auto-generated mail.";

                    emailSenderService.sendEmail(toEmail, subject, body);
                }
                else {
                    throw new EmployeeNotFoundException("Id not found of user Id"+p.getReviewerId());
                }
            }
            else {
                throw new NullPointerException("Enter Valid Id");
            }

            return p;

        } catch (Exception e) {
            System.err.println("Error while creating PIP: " + e.getMessage());
            throw new RuntimeException("Failed to create PIP.");
        }
    }

    @Override
    public Pip getPipById(Long id) {
        try {
            return pipRepository.findById(id)
                    .orElseThrow(() -> new PipNotFoundException("PIP not found with ID: " + id));
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching PIP: " + e.getMessage());
            throw new RuntimeException("Failed to fetch PIP.");
        }
    }

    @Override
    public List<Pip> getAllPips() {
        try {
            return pipRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error retrieving all PIPs: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve PIPs.");
        }
    }

    @Override
    public Pip updatePip(Long id, Pip pipDetails) {
        try {
            Pip pip = pipRepository.findById(id)
                    .orElseThrow(() -> new PipNotFoundException("PIP not found with ID: " + id));

            pip.setStartDate(pipDetails.getStartDate());
            pip.setEndDate(pipDetails.getEndDate());
            pip.setGoals(pipDetails.getGoals());
            pip.setProgress(pipDetails.getProgress());
            pip.setStatus(pipDetails.getStatus());
            pip.setReviewerId(pipDetails.getReviewerId());
            pip.setOutcome(pipDetails.getOutcome());
            pip.setComments(pipDetails.getComments());

            Pip save=pipRepository.save(pip);

            AuditLog log = new AuditLog();
            log.setUserId(save.getReviewerId());
            log.setEntityname(EntityName.PIP);
            log.setEntityId(id);
            log.setAction(Action.UPDATE);
            log.setTimestamp(LocalDateTime.now());
            log.setRemarks("Pip updated");
            auditLogService.updateAuditlogPip(log);

            Notification notification = new Notification();
            notification.setUserId(pip.getEmployee().getEmployeeId());
            notification.setTitle("PIP Ended");
            notification.setMessage("PIP Ended");
            notification.setType("INFO");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.updatePip(notification);

            if (pip.getReviewerId() != null) {
                Optional<Employee> Opt = employeeRepository.findById(pip.getReviewerId());
                if (Opt.isPresent()) {
                    Employee toEmployee = Opt.get();
                    String toEmail = toEmployee.getEmail();
                    String subject = "New PIP Notification";
                    String body = "Dear " + toEmployee.getName()+","+"\n\nI hope this message finds you a well. "+
                            "\nYou have received new PIP from User ID: " + pip.getReviewerId()+".Please Check It."+"\nIf you have any related queries feel free to reach out us."+"\n\n"
                            +"Best Regards,"+"\n "+"HR Team"+"\n\n\nThis is auto-generated mail.";

                    emailSenderService.sendEmail(toEmail, subject, body);
                }
                else {
                    throw new EmployeeNotFoundException("Id not found of user Id"+pip.getReviewerId());
                }
            }
            else {
                throw new NullPointerException("Enter Valid Id");
            }
            return save;
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error updating PIP: " + e.getMessage());
            throw new RuntimeException("Failed to update PIP with ID: " + id);
        }
    }

    @Override
    public void deletePip(Long id) {
        try {
            Pip pip = pipRepository.findById(id)
                    .orElseThrow(() -> new PipNotFoundException("PIP not found with ID: " + id));
            pipRepository.delete(pip);

            AuditLog log = new AuditLog();
            log.setUserId(id);
            log.setEntityname(EntityName.PIP);
            log.setEntityId(id);
            log.setAction(Action.DELETE);
            log.setTimestamp(LocalDateTime.now());
            log.setRemarks("PIP deleted");
            auditLogService.createAuditLogPip(log);
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error deleting PIP: " + e.getMessage());
            throw new RuntimeException("Failed to delete PIP with ID: " + id);
        }
    }

    public static void validation(Pip pip)
    {
        // Reviewer ID validation
        if(pip.getReviewerId()==null||!
        Validation.REVIEWER_ID_PATTERN.matcher(pip.getReviewerId().toString()).matches()){
            throw new IllegalArgumentException("Reviewer ID must be a valid positive number");
        }

        // Status validation
        if(pip.getStatus()==null||
                !
        Validation.STATUS_PATTERN.matcher(pip.getStatus().toString()).matches()) {
            throw new IllegalArgumentException("Status must be one of:ACTIVE,COMPLETED,FAILED");
        }

        //Goals validation
        if(pip.getGoals()==null||
                !
        Validation.GOALS_PATTERN.matcher(pip.getGoals()).matches()){
            throw new IllegalArgumentException("Goals must be 1-500 Characters,letters/numbers/punctuation only");

        }

        // Progress validation (optional)
        if (pip.getProgress() != null &&
                !Validation.PROGRESS_PATTERN.matcher(pip.getProgress()).matches()) {
            throw new IllegalArgumentException("Progress must be max 500 characters, letters/numbers/punctuation only");
        }

        // Outcome validation (optional)
        if (pip.getOutcome() != null &&
                !Validation.OUTCOME_PATTERN.matcher(pip.getOutcome()).matches()) {
            throw new IllegalArgumentException("Outcome must be max 500 characters, letters/numbers/punctuation only");
        }

        // Comments validation (optional)
        if (pip.getComments() != null &&
                !Validation.COMMENTS_PATTERN.matcher(pip.getComments()).matches()) {
            throw new IllegalArgumentException("Comments must be max 1000 characters, letters/numbers/punctuation only");
        }

        // Start Date check (not null + must be <= today)
        if (pip.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (pip.getStartDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }

        // End Date check (not null + must be >= today)
        if (pip.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }

        if (pip.getEndDate().isBefore(pip.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

    }
}

 */

package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PipRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.Service.PipService;
import com.pipTracker.Validations.Validation;
import lombok.extern.slf4j.Slf4j;   // Lombok logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PipServiceImpl implements PipService {

    @Autowired
    private PipRepository pipRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private Notificationservice notificationService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public Pip createPip(Pip pip) {
        log.info("Creating new PIP for employeeId: {}",
                pip.getEmployee() != null ? pip.getEmployee().getEmployeeId() : null);
        try {
            Pip p = pipRepository.save(pip);
            log.debug("PIP saved: {}", p);

            AuditLog logEntry = new AuditLog();
            logEntry.setUserId(p.getPipId());
            logEntry.setEntityId(p.getPipId());
            logEntry.setTimestamp(LocalDateTime.now());
            logEntry.setEntityname(EntityName.PIP);
            logEntry.setAction(Action.CREATE);
            logEntry.setRemarks("PIP Created");
            auditLogService.createAuditLogPip(logEntry);
            log.info("Audit log created for PIP ID: {}", p.getPipId());

            Notification notification = new Notification();
            notification.setUserId(p.getEmployee().getEmployeeId());
            notification.setTitle("PIP Started");
            notification.setMessage("New PIP Started");
            notification.setType("ALERT");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.createNotification(notification);
            log.info("Notification sent to employeeId: {}", p.getEmployee().getEmployeeId());

            if (p.getReviewerId() != null) {
                Optional<Employee> opt = employeeRepository.findById(p.getReviewerId());
                if (opt.isPresent()) {
                    Employee toEmployee = opt.get();
                    String toEmail = toEmployee.getEmail();
                    String subject = "New PIP Notification";
                    String body = "Dear " + toEmployee.getName() + ",\n\n"
                            + "I hope this message finds you well.\n"
                            + "You have received a new PIP from User ID: " + pip.getReviewerId() + ". Please check it.\n"
                            + "If you have any related queries feel free to reach out to us.\n\n"
                            + "Best Regards,\nHR Team\n\nThis is an auto-generated mail.";
                    emailSenderService.sendEmail(toEmail, subject, body);
                    log.info("Email sent to reviewer (ID: {}, Email: {})", p.getReviewerId(), toEmail);
                } else {
                    log.warn("Reviewer not found with ID: {}", p.getReviewerId());
                    throw new EmployeeNotFoundException("Id not found of user Id " + p.getReviewerId());
                }
            } else {
                log.error("Reviewer ID is null");
                throw new NullPointerException("Enter Valid Id");
            }

            return p;

        } catch (Exception e) {
            log.error("Error while creating PIP", e);
            throw new RuntimeException("Failed to create PIP.");
        }
    }

    @Override
    public Pip getPipById(Long id) {
        log.info("Fetching PIP with ID: {}", id);
        try {
            return pipRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PIP not found with ID: {}", id);
                        return new PipNotFoundException("PIP not found with ID: " + id);
                    });
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching PIP with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch PIP.");
        }
    }

    @Override
    public List<Pip> getAllPips() {
        log.info("Fetching all PIPs");
        try {
            List<Pip> list = pipRepository.findAll();
            log.info("Total PIPs retrieved: {}", list.size());
            return list;
        } catch (Exception e) {
            log.error("Error retrieving all PIPs", e);
            throw new RuntimeException("Failed to retrieve PIPs.");
        }
    }

    @Override
    public Pip updatePip(Long id, Pip pipDetails) {
        log.info("Updating PIP with ID: {}", id);
        try {
            Pip pip = pipRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PIP not found with ID: {}", id);
                        return new PipNotFoundException("PIP not found with ID: " + id);
                    });

            pip.setStartDate(pipDetails.getStartDate());
            pip.setEndDate(pipDetails.getEndDate());
            pip.setGoals(pipDetails.getGoals());
            pip.setProgress(pipDetails.getProgress());
            pip.setStatus(pipDetails.getStatus());
            pip.setReviewerId(pipDetails.getReviewerId());
            pip.setOutcome(pipDetails.getOutcome());
            pip.setComments(pipDetails.getComments());

            Pip save = pipRepository.save(pip);
            log.info("PIP updated successfully: {}", save);

            AuditLog logEntry = new AuditLog();
            logEntry.setUserId(save.getReviewerId());
            logEntry.setEntityname(EntityName.PIP);
            logEntry.setEntityId(id);
            logEntry.setAction(Action.UPDATE);
            logEntry.setTimestamp(LocalDateTime.now());
            logEntry.setRemarks("PIP updated");
            auditLogService.updateAuditlogPip(logEntry);
            log.info("Audit log updated for PIP ID: {}", id);

            Notification notification = new Notification();
            notification.setUserId(pip.getEmployee().getEmployeeId());
            notification.setTitle("PIP Ended");
            notification.setMessage("PIP Ended");
            notification.setType("ALERT");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.updatePip(notification);
            log.info("Notification updated for employeeId: {}", pip.getEmployee().getEmployeeId());

            if (pip.getReviewerId() != null) {
                Optional<Employee> opt = employeeRepository.findById(pip.getReviewerId());
                if (opt.isPresent()) {
                    Employee toEmployee = opt.get();
                    String toEmail = toEmployee.getEmail();
                    String subject = "New PIP Notification";
                    String body = "Dear " + toEmployee.getName() + ",\n\n"
                            + "I hope this message finds you well.\n"
                            + "You have received a new PIP from User ID: " + pip.getReviewerId() + ". Please check it.\n"
                            + "If you have any related queries feel free to reach out to us.\n\n"
                            + "Best Regards,\nHR Team\n\nThis is an auto-generated mail.";
                    emailSenderService.sendEmail(toEmail, subject, body);
                    log.info("Email sent to reviewer (ID: {}, Email: {})", pip.getReviewerId(), toEmail);
                } else {
                    log.warn("Reviewer not found with ID: {}", pip.getReviewerId());
                    throw new EmployeeNotFoundException("Id not found of user Id " + pip.getReviewerId());
                }
            } else {
                log.error("Reviewer ID is null while updating PIP");
                throw new NullPointerException("Enter Valid Id");
            }

            return save;
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating PIP with ID: {}", id, e);
            throw new RuntimeException("Failed to update PIP with ID: " + id);
        }
    }

    @Override
    public void deletePip(Long id) {
        log.info("Deleting PIP with ID: {}", id);
        try {
            Pip pip = pipRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PIP not found with ID: {}", id);
                        return new PipNotFoundException("PIP not found with ID: " + id);
                    });
            pipRepository.delete(pip);
            log.info("PIP deleted with ID: {}", id);

            AuditLog logEntry = new AuditLog();
            logEntry.setUserId(id);
            logEntry.setEntityname(EntityName.PIP);
            logEntry.setEntityId(id);
            logEntry.setAction(Action.DELETE);
            logEntry.setTimestamp(LocalDateTime.now());
            logEntry.setRemarks("PIP deleted");
            auditLogService.createAuditLogPip(logEntry);
            log.info("Audit log created for PIP deletion, ID: {}", id);

        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting PIP with ID: {}", id, e);
            throw new RuntimeException("Failed to delete PIP with ID: " + id);
        }
    }

    public static void validation(Pip pip) {
        // You can also log validation checks if needed
        log.debug("Validating PIP: {}", pip);

        if (pip.getReviewerId() == null ||
                !Validation.REVIEWER_ID_PATTERN.matcher(pip.getReviewerId().toString()).matches()) {
            throw new IllegalArgumentException("Reviewer ID must be a valid positive number");
        }

        if (pip.getStatus() == null ||
                !Validation.STATUS_PATTERN.matcher(pip.getStatus().toString()).matches()) {
            throw new IllegalArgumentException("Status must be one of: ACTIVE, COMPLETED, FAILED");
        }

        if (pip.getGoals() == null ||
                !Validation.GOALS_PATTERN.matcher(pip.getGoals()).matches()) {
            throw new IllegalArgumentException("Goals must be 1-500 characters, letters/numbers/punctuation only");
        }

        if (pip.getProgress() != null &&
                !Validation.PROGRESS_PATTERN.matcher(pip.getProgress()).matches()) {
            throw new IllegalArgumentException("Progress must be max 500 characters, letters/numbers/punctuation only");
        }

        if (pip.getOutcome() != null &&
                !Validation.OUTCOME_PATTERN.matcher(pip.getOutcome()).matches()) {
            throw new IllegalArgumentException("Outcome must be max 500 characters, letters/numbers/punctuation only");
        }

        if (pip.getComments() != null &&
                !Validation.COMMENTS_PATTERN.matcher(pip.getComments()).matches()) {
            throw new IllegalArgumentException("Comments must be max 1000 characters, letters/numbers/punctuation only");
        }

        if (pip.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (pip.getStartDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }

        if (pip.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }

        if (pip.getEndDate().isBefore(pip.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}
