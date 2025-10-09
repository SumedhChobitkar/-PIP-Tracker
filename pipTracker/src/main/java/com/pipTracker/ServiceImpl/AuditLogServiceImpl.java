package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.ArchiveStatus;
import com.pipTracker.Entity.AuditLog;
import com.pipTracker.Entity.AuditLogArchieve;
import com.pipTracker.Exception.AuditLogNotFoundException;
import com.pipTracker.Repository.AuditLogArchieveRepository;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.Service.AuditLogService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditLogServiceImpl implements AuditLogService
{
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogArchieveRepository auditLogArchieveRepository;
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    @Override
    public AuditLog createAuditLogFeedBack(AuditLog log)
    {
        try {
            validationAuditLog(log);
            return auditLogRepository.save(log);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }
    @Override
    public AuditLog createAuditLogSkillGap(AuditLog log)
    {
        try {
            validationAuditLog(log);
            return auditLogRepository.save(log);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }
    public AuditLog createAuditLogPip(AuditLog log)
    {
        try {
            validationAuditLog(log);
            return auditLogRepository.save(log);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public AuditLog createAuditLogPerformanceReview(AuditLog log) {

        try {
            validationAuditLog(log);
            return auditLogRepository.save(log);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public List<AuditLog> getAllLogs() {
        try {
            return auditLogRepository.findAll();
        }
        catch (Exception e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }
    @Override
    public List<AuditLog> getFeedBackLogsById(Long entityId) {
        try {
           return auditLogRepository.findByEntityId(entityId);
        }
        catch (Exception e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }
    @Override
    public AuditLog updateAuditlogFeedBack(AuditLog newlog) {
        try {
            return auditLogRepository.save(newlog);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public AuditLog updateAuditlogSkillGap(AuditLog newlog) {
        try {
            validationAuditLog(newlog);
            return auditLogRepository.save(newlog);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }
    @Override
    public AuditLog updateAuditlogPip(AuditLog newlog) {
        try {
            validationAuditLog(newlog);
            return auditLogRepository.save(newlog);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public AuditLog updateAuditlogPerformanceReview(AuditLog newlog) {
        try {
            validationAuditLog(newlog);
            return auditLogRepository.save(newlog);
        }
        catch (AuditLogNotFoundException e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Transactional
    @Override
    public void deleteAuditLog(Long logId) {
        try
        {
            Optional<AuditLog> optional=auditLogRepository.findById(logId);
            if(optional.isPresent())
            {
                AuditLog al=optional.get();
                AuditLogArchieve auditLogArchieve=new AuditLogArchieve();
                BeanUtils.copyProperties(al,auditLogArchieve);
                auditLogArchieve.setLogId(al.getLogId());
                auditLogArchieve.setDeletedAt(LocalDateTime.now());
                auditLogArchieve.setStatus(ArchiveStatus.DELETED);

                AuditLogArchieveServiceImpl.validationAuditLogArchieve(auditLogArchieve);
                auditLogArchieveRepository.save(auditLogArchieve);
                auditLogRepository.deleteById(logId);
            }
            else {
                throw new AuditLogNotFoundException("Error to delete it");
            }
        }
        catch (AuditLogNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    public static void validationAuditLog(AuditLog auditLog)
    {
        if(auditLog.getEntityname()==null || !Validation.entityname_PATTERN.matcher(String.valueOf(auditLog.getEntityname())).matches())
        {
            throw new IllegalArgumentException("Input Entities only which you have");
        }
        if(auditLog.getEntityId()==null || !Validation.entityId_PATTERN.matcher(auditLog.getEntityId().toString()).matches())
        {
            throw new IllegalArgumentException("Pass only Valid ID Number");
        }
        if(auditLog.getAction()==null || !Validation.action_PATTERN.matcher(String.valueOf(auditLog.getAction())).matches())
        {
            throw new IllegalArgumentException("You can add only Create,update and delete actions");
        }
        if(auditLog.getRemarks()==null || !Validation.remarks_PATTERN.matcher(auditLog.getRemarks().toString()).matches())
        {
            throw new IllegalArgumentException("Add Validate Remarks");
        }
    }
}
