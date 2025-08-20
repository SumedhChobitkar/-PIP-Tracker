package com.pipTracker.Controller;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Role;
import com.pipTracker.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/addHr")
    public ResponseEntity<?> saveHr(@RequestBody Employee employees) {
        try {
            Employee saved = employeeService.saveHr(employees);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("HR creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addManager/{hrId}")
    public ResponseEntity<?> saveManager(@RequestBody Employee employees, @PathVariable Long hrId) {
        try {
            Employee saved = employeeService.saveManager(employees, hrId);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Manager creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addEmployee/{managerId}")
    public ResponseEntity<?> saveEmployee(@RequestBody Employee employee, @PathVariable Long managerId) {
        try {
            Employee saved = employeeService.saveEmployee(employee, managerId);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Employee creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<Employee> list = employeeService.getAllEmployees();
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch employees: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            Employee emp = employeeService.getEmployeeById(id);
            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getEmployeeByName/{name}")
    public ResponseEntity<?> getEmployeeByName(@PathVariable String name) {
        try {
            Optional<Employee> employee = employeeService.getEmployeeByName(name);
            if (employee.isPresent()) {
                return new ResponseEntity<>(employee.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error while fetching employee: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/employeesUnderHr/{hrId}")
    public ResponseEntity<?> getManagersUnderHR(@PathVariable Long hrId) {
        try {
            List<Employee> managers = employeeService.getManagersUnderHR(hrId);
            return new ResponseEntity<>(managers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("HR not found with ID: " + hrId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/employeesUnderManager/{managerId}")
    public ResponseEntity<?> getEmployeesUnderManager(@PathVariable Long managerId) {
        try {
            List<Employee> employees = employeeService.getEmployeesUnderManager(managerId);
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Manager not found with ID: " + managerId, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            Employee updated = employeeService.updateEmployee(id, employee);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateRole/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Employee newRole) {
        try {
            Employee updated = employeeService.UpdateEmployeeRole(id, newRole);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Role update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

}
