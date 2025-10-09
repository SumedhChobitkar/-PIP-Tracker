package com.pipTracker.Config;


import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Role;
import com.pipTracker.Entity.Status;
import com.pipTracker.Entity.User;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDate;

@Configuration
public class StartupDataLoader {

    @Bean
    public CommandLineRunner loadInitialManager(
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String email = "superadmin@gmail.com";

            if (userRepository.findByEmail(email).isPresent()) {
                System.out.println("superAdmin  already exists.");
                return;
            }

            Employee admin = new Employee();
            admin.setName("SuperAdmin");
            admin.setEmail(email);
            // admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setDepartment("Admin");
            admin.setRole(Role.ADMIN);
            admin.setJoiningDate(LocalDate.now());
            admin.setStatus(Status.ACTIVE);

            Employee savedManager = employeeRepository.save(admin);

            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Admin@123")); // password
            user.setRole(Role.ADMIN);
            user.setEmployee(savedManager);
            user.setIsregistered("TRUE");

            userRepository.save(user);

            System.out.println("Default Super admin created: " + email + " / password: Admin@123");
        };
    }
}
