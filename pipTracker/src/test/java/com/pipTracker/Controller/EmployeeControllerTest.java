package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doThrow;

// JUnit Test for EmployeeController using Mockito standalone setup
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("John Doe");
        employee.setEmail("john@example.com");
    }

    @Test
    void testSaveHr() throws Exception {
        when(employeeService.saveHr(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees/addHr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testSaveManager() throws Exception {
        when(employeeService.saveManager(any(Employee.class), eq(1L))).thenReturn(employee);

        mockMvc.perform(post("/api/employees/addManager/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testSaveEmployee() throws Exception {
        when(employeeService.saveEmployee(any(Employee.class), eq(2L))).thenReturn(employee);

        mockMvc.perform(post("/api/employees/addEmployee/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetAllEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetEmployeeByName() throws Exception {
        when(employeeService.getEmployeeByName("John Doe")).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employees/getEmployeeByName/John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetManagersUnderHR() throws Exception {
        List<Employee> managers = Arrays.asList(employee);
        when(employeeService.getManagersUnderHR(1L)).thenReturn(managers);

        mockMvc.perform(get("/api/employees/employeesUnderHr/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetEmployeesUnderManager() throws Exception {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeService.getEmployeesUnderManager(2L)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/employeesUnderManager/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee updated = new Employee();
        updated.setEmployeeId(1L);
        updated.setName("Updated Name");
        updated.setEmail("updated@example.com");

        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testUpdateRole() throws Exception {
        Employee updated = new Employee();
        updated.setEmployeeId(1L);
        updated.setName("John Doe");
        updated.setEmail("john@example.com");

        when(employeeService.UpdateEmployeeRole(eq(1L), any(Employee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/employees/updateRole/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testUpdateRegistrationStatus() throws Exception {
        Employee updated = new Employee();
        updated.setEmployeeId(1L);
        updated.setName("John Doe");
        updated.setEmail("john@example.com");

        when(employeeService.updateRegistrationStatus(1L, true)).thenReturn(updated);

        mockMvc.perform(put("/api/employees/1/status?status=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully"));
    }




    @Test
    void testSaveHr_Failure() throws Exception {
        when(employeeService.saveHr(any(Employee.class)))
                .thenThrow(new RuntimeException("Invalid HR data"));

        mockMvc.perform(post("/api/employees/addHr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("HR creation failed: Invalid HR data"));
    }

    @Test
    void testSaveManager_Failure() throws Exception {
        when(employeeService.saveManager(any(Employee.class), eq(1L)))
                .thenThrow(new RuntimeException("HR not found"));

        mockMvc.perform(post("/api/employees/addManager/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Manager creation failed: HR not found"));
    }

    @Test
    void testSaveEmployee_Failure() throws Exception {
        when(employeeService.saveEmployee(any(Employee.class), eq(2L)))
                .thenThrow(new RuntimeException("Manager not found"));

        mockMvc.perform(post("/api/employees/addEmployee/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee creation failed: Manager not found"));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new RuntimeException("Employee not found"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found with ID: 99"));
    }

    @Test
    void testGetEmployeeByName_NotFound() throws Exception {
        when(employeeService.getEmployeeByName("Unknown"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/getEmployeeByName/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found"));
    }

    @Test
    void testGetManagersUnderHR_NotFound() throws Exception {
        when(employeeService.getManagersUnderHR(99L))
                .thenThrow(new RuntimeException("HR not found"));

        mockMvc.perform(get("/api/employees/employeesUnderHr/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("HR not found with ID: 99"));
    }

    @Test
    void testGetEmployeesUnderManager_NotFound() throws Exception {
        when(employeeService.getEmployeesUnderManager(99L))
                .thenThrow(new RuntimeException("Manager not found"));

        mockMvc.perform(get("/api/employees/employeesUnderManager/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Manager not found with ID: 99"));
    }

    @Test
    void testUpdateEmployee_Failure() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(Employee.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Update failed: Update failed"));
    }

    @Test
    void testUpdateRole_Failure() throws Exception {
        when(employeeService.UpdateEmployeeRole(eq(1L), any(Employee.class)))
                .thenThrow(new RuntimeException("Role update failed"));

        mockMvc.perform(put("/api/employees/updateRole/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Role update failed: Role update failed"));
    }

    @Test
    void testDeleteEmployee_NotFound() throws Exception {
        doThrow(new RuntimeException("Employee not found")).when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found with ID: 99"));
    }

}
