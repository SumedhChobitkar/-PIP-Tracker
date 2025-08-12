package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.User;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(User user) {
        try {
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("User already registered with email: " + user.getEmail());
            }
            Long empId = user.getEmployee().getEmployeeId();
            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));

            if (!employee.getEmail().equals(user.getEmail())) {
                throw new RuntimeException("User email and Employee email do not match");
            }

            if (!employee.getRole().equals(user.getRole())) {
                throw new RuntimeException("User Role and Employee Role do not match");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmployee(employee);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while registering user: " + e.getMessage());
        }
    }

//    @Override
//    public User loginUser(String email,String password) {
//        try {
//            Optional<User>user=userRepository.findByEmail(email);
//            if (user.isPresent()) {
//                if (user.get().getPassword().equals(password)) {
//                    return user.get();
//                } else {
//                    throw new RuntimeException("Invalid Password");
//                }
//            } else{
//                throw new RuntimeException("User not found");
//                }
//        }catch (Exception e){
//            logger.info("Error during login" + e.getMessage());
//            throw e;
//        }
//    }


    @Override
    public User loginUser(String email, String password) {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                // Use passwordEncoder.matches to compare raw password with encoded password
                if (passwordEncoder.matches(password, user.get().getPassword())) {
                    return user.get();
                } else {
                    throw new RuntimeException("Invalid Password");
                }
            } else {
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.info("Error during login: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByEmployeeId(Long employeeId) {
        try {
            Optional<User> user = userRepository.findByEmployeeEmployeeId(employeeId);
            if (user.isPresent()) {
                return user.get();
            } else {
                throw new RuntimeException("User not found with employeeId: " + employeeId);
            }
        } catch (Exception e) {
            logger.info("Error while finding user by employeeId: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<User> getUserByName(String name) {
        try {
            Optional<User> user = userRepository.findByName(name);
            if (user.isPresent()) {
                return user;
            } else {
                throw new RuntimeException("User not found with name: " + name);
            }
        } catch (Exception e) {
            System.out.println("Error while fetching user by name: " + e.getMessage());
            return Optional.empty();
        }
    }


}
