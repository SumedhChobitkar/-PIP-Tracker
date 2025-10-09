package com.pipTracker.ServiceImpl;

import com.pipTracker.CommonUtil.ValidationClass;
import com.pipTracker.Entity.RegistrationStatus;
import com.pipTracker.Entity.Status;
import com.pipTracker.Entity.User;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.EmployeeService;
import com.pipTracker.Entity.Employee;

import com.pipTracker.Repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);


    @Override
    public Employee saveEmployee(Employee employee, Long managerId) {
        try {
            validateEmployee(employee);
            employee.setManagerId(managerId);
            employee.setStatus(Status.ACTIVE);
            return employeeRepository.save(employee);
        } catch (Exception e) {
            logger.error("Error saving employee: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public Employee saveManager(Employee employees,Long hrId) {
        try {
            validateEmployee(employees);
            employees.setHrId(hrId);
            employees.setStatus(Status.ACTIVE);
            return employeeRepository.save(employees);
        } catch (Exception e) {
            logger.error("Error saving employee: {} " , e.getMessage());
            throw e;
        }
    }

    @Override
    public Employee saveHr(Employee employees) {
        try {
            validateEmployee(employees);
            employees.setStatus(Status.ACTIVE);
            return employeeRepository.save(employees);
        } catch (Exception e) {
            logger.info("Error saving employee: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        try {
            return employeeRepository.findAll();
        } catch (Exception e) {
            logger.info("Error fetching employees: " + e.getMessage());
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
            logger.info(e.getMessage());
            throw e;
        }
    }


    @Override
    public List<Employee> getManagersUnderHR(Long hrId) {
        try {
            List<Employee> managers= employeeRepository.findByHrId(hrId);
            if (managers.isEmpty()){
                throw new EmployeeNotFoundException("No managers under HR with ID:" +hrId);
            }
            return managers;
        } catch (Exception e) {
            logger.info("Error fetching managers under HR: " + e.getMessage());
            throw e;
        }
    }


    public List<Employee> getEmployeesUnderManager(Long managerId) {
        try {
            List<Employee> employees= employeeRepository.findByManagerId(managerId);
           if (employees.isEmpty()){
               throw new EmployeeNotFoundException("No employee found under manager with ID:" +managerId);
           }
           return employees;
        } catch (Exception e) {
            logger.info("Error fetching employees under manager: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Employee> getEmployeesByHrId(Long hrId) {
        List<Employee> list = employeeRepository.findByHrId(hrId);
        if (list.isEmpty()) {
            throw new RuntimeException("No employees found under HR with ID: " + hrId);
        }
        return list;
    }


    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        try {
            validateEmployee(employee);
            Employee existing = getEmployeeById(id);
            existing.setName(employee.getName());
            existing.setEmail(employee.getEmail());
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

            Optional<User> optionalUser = userRepository.findByEmployee(existing);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setEmail(employee.getEmail());
                userRepository.save(user);
            }

            return employeeRepository.save(existing);
        } catch (Exception e) {
            logger.info("Error updating employee: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Employee> getEmployeeByName(String name) {
        try {
            Optional<Employee> employee = employeeRepository.findByName(name);
            if (employee.isPresent()) {
                return employee;
            } else {
                throw new RuntimeException("Employee not found with name: " + name);
            }
        } catch (Exception e) {
            logger.info("Error while fetching employee by name: " + e.getMessage());
            return Optional.empty();
        }
    }

@Override
public Employee UpdateEmployeeRole(Long id, Employee newRole){
        try {
            Employee employee= employeeRepository.findById(id)
                    .orElseThrow(()-> new RuntimeException("Employee not found with Id" + id));
            employee.setRole(newRole.getRole());
            return employeeRepository.save(employee);
        } catch (Exception e){
            throw new RuntimeException("Error while updating employee role:" +e.getMessage());

        }
}


    @Override
    public Employee updateStatus(Long employeeId, Employee employee) {
        try {
            Employee emp = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));
            emp.setStatus(employee.getStatus());


            return employeeRepository.save(emp);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating registration status: " + e.getMessage());
        }
    }




    @Override
    public void deleteEmployee(Long id) {
        try {
            Employee emp = getEmployeeById(id);
            employeeRepository.delete(emp);
        } catch (Exception e) {
            logger.info("Error deleting employee: " + e.getMessage());
            throw e;
        }
    }


    private void validateEmployee(Employee employee) {
        if (employee.getName() == null ||
                !ValidationClass.NAME_PATTERN.matcher(employee.getName()).matches()) {
            throw new IllegalArgumentException("Invalid name format");
        }
        if (employee.getEmail() == null ||
                !ValidationClass.EMAIL_PATTERN.matcher(employee.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (employee.getDepartment() != null &&
                !ValidationClass.DEPARTMENT_PATTERN.matcher(employee.getDepartment()).matches()) {
            throw new IllegalArgumentException("Invalid department format");
        }
        if (employee.getDesignation() != null &&
                !ValidationClass.DESIGNATION_PATTERN.matcher(employee.getDesignation()).matches()) {
            throw new IllegalArgumentException("Invalid designation format");
        }
        if (employee.getSkills() != null &&
                !ValidationClass.SKILLS_PATTERN.matcher(employee.getSkills()).matches()) {
            throw new IllegalArgumentException("Invalid skills format");
        }

        if (employee.getPhotoUrl() != null &&
                !ValidationClass.PHOTO_URL_PATTERN.matcher(employee.getPhotoUrl()).matches()) {
            throw new IllegalArgumentException("Invalid photo URL format");
        }


    }

}
