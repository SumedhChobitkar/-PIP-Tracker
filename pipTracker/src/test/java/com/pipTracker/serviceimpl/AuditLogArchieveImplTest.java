package com.pipTracker.serviceimpl;

import com.pipTracker.Repository.AuditLogArchieveRepository;
import com.pipTracker.ServiceImpl.AuditLogArchieveServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuditLogArchieveImplTest {

    @Mock
    AuditLogArchieveRepository auditLogArchieveRepository;

    @InjectMocks
    AuditLogArchieveServiceImpl auditLogArchieveService;

    @Test
    void getAllArchieveImpl()
    {

    }
}
