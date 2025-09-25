package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.ArchiveStatus;
import com.pipTracker.Entity.AuditLog;
import com.pipTracker.Entity.AuditLogArchieve;
import com.pipTracker.Exception.AuditLogArchieveNotFoundException;
import com.pipTracker.Repository.AuditLogArchieveRepository;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.Service.AuditLogArchieveService;
import com.pipTracker.Validations.Validation;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditLogArchieveServiceImpl implements AuditLogArchieveService
{
    @Autowired
    private AuditLogArchieveRepository auditLogArchieveRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AuditLogArchieveServiceImpl.class);

    @Override
    public List<AuditLogArchieve> getAllArchieve(Long logId) {
       try
       {
          List<AuditLogArchieve>list=auditLogArchieveRepository.findByLogId(logId);
          if(list.isEmpty())
          {
              throw new AuditLogArchieveNotFoundException(logId+" ID no found.");
          }
          else
          {
              return list;
          }
       }
       catch (Exception e)
       {
           logger.info("Error"+e.getMessage());
           throw e;
       }
    }

    @Override
    @Transactional
    public AuditLogArchieve updateArchieve(Long logId) {
        try {
            AuditLogArchieve archive = auditLogArchieveRepository.findById(logId)
                    .orElseThrow(() -> new RuntimeException("Archived AuditLog not found with id " + logId));

            if (archive.getStatus() != ArchiveStatus.DELETED) {
                throw new AuditLogArchieveNotFoundException("Only DELETED logs can be restored!!!!!!");
            }

            AuditLog log = new AuditLog();
            log.setLogId(null);
            BeanUtils.copyProperties(archive, log,"logId");

            validationAuditLogArchieve(archive);
            auditLogRepository.save(log);

            archive.setStatus(ArchiveStatus.RESTORED);
            return auditLogArchieveRepository.save(archive);
        }
        catch (Exception e)
        {
            logger.info("Error:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteAll()
    {
     try
     {
         auditLogArchieveRepository.deleteAll();;
     }
     catch (Exception e)
     {
         logger.info(e.getMessage());
         throw  e;
     }
    }

    @Override
    public void deleteAuditLogArchieve(Long logId) {
        try
        {
            Optional<AuditLogArchieve> auditLogArchieve=auditLogArchieveRepository.findById(logId);
            if(auditLogArchieve.isPresent())
            {
                auditLogArchieveRepository.deleteById(logId);
            }
            else {
                throw new AuditLogArchieveNotFoundException(logId+" id not found in Archieve");
            }
        } catch (Exception e) {
            logger.info("ERROR:"+e.getMessage());
            throw e;
        }
    }
    public static void validationAuditLogArchieve(AuditLogArchieve auditLogArchieve)
    {
        if(auditLogArchieve.getEntityname()==null || !Validation.entityname_PATTERN.matcher(String.valueOf(auditLogArchieve.getEntityname())).matches())
        {
            throw new IllegalArgumentException("Input Entities only which you have");
        }
        if(auditLogArchieve.getEntityId()==null || !Validation.entityId_PATTERN.matcher(auditLogArchieve.getEntityId().toString()).matches())
        {
            throw new IllegalArgumentException("Pass only Valid ID Number");
        }
        if(auditLogArchieve.getAction()==null || !Validation.action_PATTERN.matcher(String.valueOf(auditLogArchieve.getAction())).matches())
        {
            throw new IllegalArgumentException("You can add only Create,update and delete actions");
        }
        if(auditLogArchieve.getRemarks()==null || !Validation.remarks_PATTERN.matcher(auditLogArchieve.getRemarks().toString()).matches())
        {
            throw new IllegalArgumentException("Add Validate Remarks");
        }
        if(auditLogArchieve.getStatus()==null || !Validation.archieveStatus_PATTERN.matcher(String.valueOf(auditLogArchieve.getStatus())).matches())
        {
            throw new IllegalArgumentException("Add Valid Status");
        }
    }
}
