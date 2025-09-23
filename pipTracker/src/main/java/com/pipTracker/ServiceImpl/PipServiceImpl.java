
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
            notification.setType("ALERT");
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
}


