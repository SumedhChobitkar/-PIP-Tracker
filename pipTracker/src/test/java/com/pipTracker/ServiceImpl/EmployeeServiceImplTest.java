package com.pipTracker.ServiceImpl;

import com.pipTracker.CommonUtil.ValidationClass;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.RegistrationStatus;
import com.pipTracker.Entity.Role;
import com.pipTracker.Entity.User;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest

public class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        ReflectionTestUtils.setField(employee, "employeeId", 1L);
        employee.setName("Shilpa");
        employee.setEmail("shilpa@test.com");
        employee.setDepartment("IT");
        employee.setDesignation("Developer");
        employee.setSkills("Java");
        employee.setStatus("Active");

    }

    @Test
    void testSaveEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Employee saved = employeeService.saveEmployee(employee, 2L);

        assertNotNull(saved);
        assertEquals("Shilpa", saved.getName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testSaveManager() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Employee saved = employeeService.saveManager(employee, 10L);

        assertEquals("Shilpa", saved.getName());
        assertEquals("shilpa@test.com", saved.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testSaveHr() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Employee saved = employeeService.saveHr(employee);

        assertEquals("Shilpa", saved.getName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));
        List<Employee> employees = employeeService.getAllEmployees();

        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
    }

    @Test
    void testGetEmployeeById_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Employee emp = employeeService.getEmployeeById(1L);

        assertNotNull(emp);
        assertEquals("Shilpa", emp.getName());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(99L));
    }

    @Test
    void testGetManagersUnderHR() {
        when(employeeRepository.findByHrId(1L)).thenReturn(List.of(employee));
        List<Employee> managers = employeeService.getManagersUnderHR(1L);

        assertFalse(managers.isEmpty());
    }

    @Test
    void testGetManagersUnderHR_NotFound() {
        when(employeeRepository.findByHrId(1L)).thenReturn(Collections.emptyList());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getManagersUnderHR(1L));
    }

    @Test
    void testGetEmployeesUnderManager() {
        when(employeeRepository.findByManagerId(2L)).thenReturn(List.of(employee));
        List<Employee> list = employeeService.getEmployeesUnderManager(2L);

        assertEquals(1, list.size());
    }

    @Test
    void testGetEmployeesUnderManager_NotFound() {
        when(employeeRepository.findByManagerId(2L)).thenReturn(Collections.emptyList());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeesUnderManager(2L));
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedData = new Employee();
        updatedData.setName("Updated");
        updatedData.setEmail("updated@test.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(userRepository.findByEmployee(employee)).thenReturn(Optional.of(new User()));

        Employee updated = employeeService.updateEmployee(1L, updatedData);

        assertNotNull(updated);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetEmployeeByName_Found() {
        when(employeeRepository.findByName("Shilpa")).thenReturn(Optional.of(employee));
        Optional<Employee> result = employeeService.getEmployeeByName("Shilpa");

        assertTrue(result.isPresent());
        assertEquals("Shilpa", result.get().getName());
    }

    @Test
    void testGetEmployeeByName_NotFound() {
        when(employeeRepository.findByName("Unknown")).thenReturn(Optional.empty());
        Optional<Employee> result = employeeService.getEmployeeByName("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateEmployeeRole() {
        Employee newRole = new Employee();
        newRole.setRole(Role.MANAGER);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.UpdateEmployeeRole(1L, newRole);

        assertNotNull(result);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testUpdateRegistrationStatus() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.updateRegistrationStatus(1L, true);

        assertEquals(RegistrationStatus.REGISTERED, result.getIsRegistered());
    }

    @Test
    void testDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testSaveEmployee_InvalidName() {
        employee.setName("123Invalid"); // Invalid name (digits not allowed)
        assertThrows(IllegalArgumentException.class, () -> employeeService.saveEmployee(employee, 2L));
    }

    @Test
    void testSaveEmployee_InvalidEmail() {
        employee.setEmail("invalid-email"); // Invalid email format
        assertThrows(IllegalArgumentException.class, () -> employeeService.saveEmployee(employee, 2L));
    }

    @Test
    void testSaveHr_InvalidDepartment() {
        employee.setDepartment("Dept#"); // invalid department (not matching regex)
        assertThrows(IllegalArgumentException.class, () -> employeeService.saveHr(employee));
    }

    @Test
    void testGetEmployeeById_Exception() {
        when(employeeRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        Employee newData = new Employee();
        newData.setName("XYZ");
        newData.setEmail("xyz@test.com");

        when(employeeRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(100L, newData));
    }

    @Test
    void testUpdateEmployeeRole_NotFound() {
        Employee newRole = new Employee();
        newRole.setRole(Role.HR);

        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.UpdateEmployeeRole(99L, newRole));
    }

    @Test
    void testUpdateRegistrationStatus_NotFound() {
        when(employeeRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.updateRegistrationStatus(50L, true));
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(200L));
    }

    @Test
    void testGetEmployeeByName_RuntimeException() {
        when(employeeRepository.findByName("Invalid")).thenThrow(new RuntimeException("DB error"));

        Optional<Employee> result = employeeService.getEmployeeByName("Invalid");
        assertTrue(result.isEmpty()); // method returns Optional.empty() in catch block
    }


}
