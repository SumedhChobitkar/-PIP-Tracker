package com.pipTracker.ServiceImpl;

import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Service.EmployeeService;
import com.pipTracker.Entity.Employee;

import com.pipTracker.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee saveEmployee(Employee employee) {
        try {
            return employeeRepository.save(employee);
        } catch (Exception e) {
            System.out.println("Error saving employee: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        try {
            return employeeRepository.findAll();
        } catch (Exception e) {
            System.out.println("Error fetching employees: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Employee getEmployeeById(Long id) {
        try {
            Optional<Employee> optional = employeeRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            } else {
                throw new EmployeeNotFoundException("Employee not found with ID: " + id);
            }
        } catch (EmployeeNotFoundException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        try {
            Employee existing = getEmployeeById(id);
            existing.setName(employee.getName());
            existing.setEmail(employee.getEmail());
            existing.setPassword(employee.getPassword());
            existing.setRole(employee.getRole());
            existing.setDepartment(employee.getDepartment());
            existing.setDesignation(employee.getDesignation());
            existing.setSkills(employee.getSkills());
            existing.setCurrentKRA(employee.getCurrentKRA());
            existing.setKpi(employee.getKpi());
            existing.setManagerId(employee.getManagerId());
            existing.setPhotoUrl(employee.getPhotoUrl());
            existing.setJoiningDate(employee.getJoiningDate());
            existing.setStatus(employee.getStatus());

            return employeeRepository.save(existing);
        } catch (Exception e) {
            System.out.println("Error updating employee: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteEmployee(Long id) {
        try {
            Employee emp = getEmployeeById(id);
            employeeRepository.delete(emp);
        } catch (Exception e) {
            System.out.println("Error deleting employee: " + e.getMessage());
            throw e;
        }
    }


}
