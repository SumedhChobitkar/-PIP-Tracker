package com.pipTracker.Service;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Role;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmployeeService {
    Employee saveEmployee(Employee employee,Long managerId);
    Employee saveManager(Employee employee,Long hrId);
    Employee saveHr(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee updateEmployee(Long id, Employee employee);
    Optional<Employee> getEmployeeByName(String name);
    List<Employee> getManagersUnderHR(Long hrId);
    List<Employee> getEmployeesUnderManager(Long managerId);
    List<Employee> getEmployeesByHrId(Long hrId);
    public Employee UpdateEmployeeRole(Long id, Employee newRole);
    Employee updateRegistrationStatus(Long employeeId, boolean status);


    void deleteEmployee(Long id);
}
