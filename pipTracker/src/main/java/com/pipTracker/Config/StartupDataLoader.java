package com.pipTracker.Config;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Role;
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

            // Check if super admin exists
            if (userRepository.findByEmail(email).isPresent()) {
                System.out.println("SuperAdmin already exists.");
                return;
            }

            // --- Create Employee ---
            Employee admin = Employee.builder()
                    .name("SuperAdmin")
                    .email(email)
                    .department("Admin")
                    .role(Role.ADMIN)
                    .joiningDate(LocalDate.now())
                    .status("Active")
                    .build();

            Employee savedManager = employeeRepository.save(admin);

            // --- Create User with builder ---
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .employee(savedManager)
                    .isRegistered("TRUE")  // <-- correct setter
                    .build();

            userRepository.save(user);

            System.out.println("Default Super admin created: " + email + " / password: Admin@123");
        };
    }
}
