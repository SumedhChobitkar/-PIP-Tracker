/*
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

    @PutMapping("/{id}/status")
    public ResponseEntity<Employee> updateRegistrationStatus(
            @PathVariable Long id,
            @RequestParam boolean status) {
        Employee updatedEmployee = employeeService.updateRegistrationStatus(id, status);
        return ResponseEntity.ok(updatedEmployee);
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
 */

package com.pipTracker.Controller;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Employee APIs",
        description = "Operations related to HR, Manager, and Employee management"
)
@RestController
@CrossOrigin("*")
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(
            summary = "Add HR",
            description = "Creates a new HR.\n\nEg: POST http://localhost:8080/api/employees/addHr"
    )
    @ApiResponse(responseCode = "201", description = "HR created successfully")
    @ApiResponse(responseCode = "400", description = "HR creation failed")
    @PostMapping("/addHr")
    public ResponseEntity<?> saveHr(@RequestBody Employee employees) {
        try {
            Employee saved = employeeService.saveHr(employees);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("HR creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Add Manager under HR",
            description = "Creates a new Manager under a given HR.\n\nEg: POST http://localhost:8080/api/employees/addManager/{hrId}"
    )
    @ApiResponse(responseCode = "201", description = "Manager created successfully")
    @ApiResponse(responseCode = "400", description = "Manager creation failed")
    @PostMapping("/addManager/{hrId}")
    public ResponseEntity<?> saveManager(@RequestBody Employee employees, @PathVariable Long hrId) {
        try {
            Employee saved = employeeService.saveManager(employees, hrId);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Manager creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Add Employee under Manager",
            description = "Creates a new Employee under a given Manager.\n\nEg: POST http://localhost:8080/api/employees/addEmployee/{managerId}"
    )
    @ApiResponse(responseCode = "201", description = "Employee created successfully")
    @ApiResponse(responseCode = "400", description = "Employee creation failed")
    @PostMapping("/addEmployee/{managerId}")
    public ResponseEntity<?> saveEmployee(@RequestBody Employee employee, @PathVariable Long managerId) {
        try {
            Employee saved = employeeService.saveEmployee(employee, managerId);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Employee creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Get All Employees",
            description = "Fetches all employees from the system.\n\nEg: GET http://localhost:8080/api/employees/getAll"
    )
    @ApiResponse(responseCode = "200", description = "Employees fetched successfully")
    @ApiResponse(responseCode = "404", description = "No employees found")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<Employee> list = employeeService.getAllEmployees();
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch employees: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Get Employee by ID",
            description = "Fetches employee details by ID.\n\nEg: GET http://localhost:8080/api/employees/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Employee fetched successfully")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            Employee emp = employeeService.getEmployeeById(id);
            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Get Employee by Name",
            description = "Fetches employee details by name.\n\nEg: GET http://localhost:8080/api/employees/getEmployeeByName/{name}"
    )
    @ApiResponse(responseCode = "200", description = "Employee fetched successfully")
    @ApiResponse(responseCode = "404", description = "Employee not found")
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

    @Operation(
            summary = "Get Managers under HR",
            description = "Fetches all Managers reporting to a given HR.\n\nEg: GET http://localhost:8080/api/employees/employeesUnderHr/{hrId}"
    )
    @ApiResponse(responseCode = "200", description = "Managers fetched successfully")
    @ApiResponse(responseCode = "404", description = "HR not found")
    @GetMapping("/employeesUnderHr/{hrId}")
    public ResponseEntity<?> getManagersUnderHR(@PathVariable Long hrId) {
        try {
            List<Employee> managers = employeeService.getManagersUnderHR(hrId);
            return new ResponseEntity<>(managers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("HR not found with ID: " + hrId, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Get Employees under Manager",
            description = "Fetches all Employees reporting to a given Manager.\n\nEg: GET http://localhost:8080/api/employees/employeesUnderManager/{managerId}"
    )
    @ApiResponse(responseCode = "200", description = "Employees fetched successfully")
    @ApiResponse(responseCode = "404", description = "Manager not found")
    @GetMapping("/employeesUnderManager/{managerId}")
    public ResponseEntity<?> getEmployeesUnderManager(@PathVariable Long managerId) {
        try {
            List<Employee> employees = employeeService.getEmployeesUnderManager(managerId);
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Manager not found with ID: " + managerId, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Update Employee",
            description = "Updates employee details by ID.\n\nEg: PUT http://localhost:8080/api/employees/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @ApiResponse(responseCode = "400", description = "Update failed")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            Employee updated = employeeService.updateEmployee(id, employee);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Update Employee Role",
            description = "Updates the role of an employee.\n\nEg: PUT http://localhost:8080/api/employees/updateRole/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    @ApiResponse(responseCode = "400", description = "Role update failed")
    @PutMapping("/updateRole/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Employee newRole) {
        try {
            Employee updated = employeeService.UpdateEmployeeRole(id, newRole);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Role update failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Update Employee Registration Status",
            description = "Updates the registration status of an employee.\n\nEg: PUT http://localhost:8080/api/employees/{id}/status?status=true"
    )
    @ApiResponse(responseCode = "200", description = "Registration status updated successfully")
    @PutMapping("/{id}/status")
    public ResponseEntity<Employee> updateRegistrationStatus(
            @PathVariable Long id,
            @RequestParam boolean status) {
        Employee updatedEmployee = employeeService.updateRegistrationStatus(id, status);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Operation(
            summary = "Delete Employee",
            description = "Deletes an employee by ID.\n\nEg: DELETE http://localhost:8080/api/employees/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Employee deleted successfully")
    @ApiResponse(responseCode = "404", description = "Employee not found")
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

