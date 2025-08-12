package com.pipTracker.Repository;

import com.pipTracker.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pipTracker.Entity.Role;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByName(String name);
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findByHrId(Long hrId);






}
