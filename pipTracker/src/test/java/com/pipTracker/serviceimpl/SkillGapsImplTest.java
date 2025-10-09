
package com.pipTracker.ServiceImpl;

package com.pipTracker.serviceimpl;


import com.pipTracker.Entity.*;
import com.pipTracker.Exception.SkillGapAnalysisNotfoundException;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.SkillGapAnalysisRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.ServiceImpl.skillgapserviceimpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillGapServiceImplTest {

    @Mock
    private SkillGapAnalysisRepository skillGapRepo;

    @Mock
    private EmployeeRepository employeeRepo;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private skillgapserviceimpl skillGapService;

    private SkillGapAnalysis skillGap;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmployeeId(1L);

        skillGap = new SkillGapAnalysis();
        skillGap.setAnalysisId(10L);
        skillGap.setSkill("Java");
        skillGap.setCurrentLevel(5);
        skillGap.setSuggestedTraining("Spring Boot");
        skillGap.setEmployee(employee);
    }

    @Test
    void testAddSkillGap_Success() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(skillGapRepo.save(any(SkillGapAnalysis.class))).thenReturn(skillGap);

        SkillGapAnalysis result = skillGapService.addSkillGap(1L, skillGap);

        assertNotNull(result);
        verify(skillGapRepo, times(1)).save(any(SkillGapAnalysis.class));
        verify(auditLogService, times(1)).createAuditLogSkillGap(any(AuditLog.class));
    }

    @Test
    void testAddSkillGap_EmployeeNotFound() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.empty());

        SkillGapAnalysisNotfoundException ex = assertThrows(
                SkillGapAnalysisNotfoundException.class,
                () -> skillGapService.addSkillGap(1L, skillGap)
        );

        assertTrue(ex.getMessage().contains("Employee Not Found"));
        verify(skillGapRepo, never()).save(any());
    }

    @Test
    void testGetSkillGapsByEmployee() {
        when(skillGapRepo.findByEmployee_EmployeeId(1L)).thenReturn(List.of(skillGap));

        List<SkillGapAnalysis> result = skillGapService.getSkillGapsByEmployee(1L);

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getSkill());
    }

    @Test
    void testGetAllSkillGapsByManager_Success() {
        employee.setSkillGaps(List.of(skillGap));
        when(employeeRepo.findByManagerId(5L)).thenReturn(List.of(employee));

        List<SkillGapAnalysis> result = skillGapService.getAllSkillGapsByManager(5L);

        assertEquals(1, result.size());
        verify(employeeRepo, times(1)).findByManagerId(5L);
    }

    @Test
    void testGetAllSkillGapsByManager_NotFound() {
        when(employeeRepo.findByManagerId(5L)).thenReturn(Collections.emptyList());

        assertThrows(SkillGapAnalysisNotfoundException.class,
                () -> skillGapService.getAllSkillGapsByManager(5L));
    }

    @Test
    void testUpdateSkillGap_Success() {
        when(skillGapRepo.findById(10L)).thenReturn(Optional.of(skillGap));
        when(skillGapRepo.save(any(SkillGapAnalysis.class))).thenReturn(skillGap);

        SkillGapAnalysis result = skillGapService.updateSkillGap(1L, skillGap);

        assertNotNull(result);
        verify(skillGapRepo, times(1)).save(any());
        verify(auditLogService, times(1)).updateAuditlogSkillGap(any(AuditLog.class));
    }

    @Test
    void testUpdateSkillGap_WrongEmployee() {
        Employee anotherEmp = new Employee();
        anotherEmp.setEmployeeId(2L);
        skillGap.setEmployee(anotherEmp);
        when(skillGapRepo.findById(10L)).thenReturn(Optional.of(skillGap));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> skillGapService.updateSkillGap(1L, skillGap));

        assertTrue(ex.getMessage().contains("does not belong"));
    }

    @Test
    void testDeleteSkillGapByEmployee_Success() {
        when(skillGapRepo.findById(10L)).thenReturn(Optional.of(skillGap));

        skillGapService.deleteSkillGapByEmployee(1L, 10L);

        verify(skillGapRepo, times(1)).delete(skillGap);
        verify(auditLogService, times(1)).createAuditLogSkillGap(any(AuditLog.class));
    }

    @Test
    void testDeleteSkillGapByEmployee_NotFound() {
        when(skillGapRepo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(SkillGapAnalysisNotfoundException.class,
                () -> skillGapService.deleteSkillGapByEmployee(1L, 10L));
    }

    @Test
    void testDeleteAllSkillGapByEmployee_Success() {
        when(skillGapRepo.findByEmployee_EmployeeId(1L)).thenReturn(List.of(skillGap));

        skillGapService.deleteAllSkillGapByEmployee(1L);

        verify(skillGapRepo, times(1)).deleteAll(anyList());
    }

    @Test
    void testDeleteAllSkillGapByEmployee_NotFound() {
        when(skillGapRepo.findByEmployee_EmployeeId(1L)).thenReturn(Collections.emptyList());

        assertThrows(SkillGapAnalysisNotfoundException.class,
                () -> skillGapService.deleteAllSkillGapByEmployee(1L));
    }
}
