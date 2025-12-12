package com.pipTracker.Repository;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Status;
import com.pipTracker.Entity.User;
import jdk.jshell.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User>findByEmail( String email);
    Optional<User> findByEmployeeEmployeeId(Long employeeId);
    Optional<User> findByEmployee(Employee employee);
    Optional<User> findByName(String name);
//    User findByStatus(Status status);

}
