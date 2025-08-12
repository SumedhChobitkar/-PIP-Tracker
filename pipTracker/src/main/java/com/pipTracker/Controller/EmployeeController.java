package com.pipTracker.Controller;

import com.pipTracker.Entity.Employee;
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

    @PostMapping("/addEmployee/{managerId}")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee,@PathVariable Long managerId) {
        Employee saved = employeeService.saveEmployee(employee,managerId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/addManager/{hrId}")
    public ResponseEntity<Employee> saveManager(@RequestBody Employee employees,@PathVariable Long hrId) {
        Employee saved = employeeService.saveManager(employees,hrId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/addHr")
    public ResponseEntity<Employee> saveHr(@RequestBody Employee employees) {
        Employee saved = employeeService.saveHr(employees);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> list = employeeService.getAllEmployees();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee emp = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(emp, HttpStatus.OK);
    }

    @GetMapping("/getEmployeeByName/{name}")
    public ResponseEntity<?> getEmployeeByName(@PathVariable String name) {
        Optional<Employee> employee = employeeService.getEmployeeByName(name);
        if (employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/employeesUnderHr/{hrId}")
    public ResponseEntity<List<Employee>> getManagersUnderHR(@PathVariable Long hrId) {
        List<Employee> managers = employeeService.getManagersUnderHR(hrId);
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }

    @GetMapping("/employeesUnderManager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesUnderManager(@PathVariable Long managerId) {
        try {
            List<Employee> employees = employeeService.getEmployeesUnderManager(managerId);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updated = employeeService.updateEmployee(id, employee);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
    }
}
