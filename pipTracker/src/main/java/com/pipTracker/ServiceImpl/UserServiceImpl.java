package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.User;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public User registerUser(User user, MultipartFile file) {
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


            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png")) {
                    user.setPhotoUrl(file.getBytes());  // store as byte[]
                    user.setFileType(contentType);
                } else {
                    throw new RuntimeException("Invalid file format. Only JPEG, JPG, PNG allowed.");
                }
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
                    if(email!=null)
                    {
                        Optional<Employee> opt = employeeRepository.findByEmail(email);
                        if(opt.isPresent()) {
                            Employee emp=opt.get();
                            String toEmail = email;
                            String subject = "No Reply";
                            String body = "Dear " + emp.getName()+ "," + "\n\nI hope this message finds you a well. " +
                                    "\nYou have Logged in on "+LocalDateTime.now()+"."+ "\nIf you have any related queries feel free to reach out us." + "\n\n"
                                    + "Best Regards," + "\n" +"HR Team."+"\n\n\nThis is auto-generated mail."
                                    ;

                            emailSenderService.sendEmail(toEmail, subject, body);
                        }
                    }
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

    @Override
    public String updatePassword(Long employeeId, String newPassword) {
        try {
            User user = userRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + employeeId));
            if (user.isRecentlyChangedPassword()) {

                LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);

                if (user.getLastPasswordChangedAt() != null &&
                        user.getLastPasswordChangedAt().isAfter(fiveDaysAgo)) {
                    return " You cannot change password within 5 days!";
                } else {
                    user.setRecentlyChangedPassword(false);
                }
            }
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return "New password cannot be the same as the old password!";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setRecentlyChangedPassword(true);
            user.setLastPasswordChangedAt(LocalDateTime.now());
            userRepository.save(user);

            return "Password updated successfully!";
        } catch (Exception e) {
            return " Error updating password: " + e.getMessage();
        }
    }

    @Override
    public User uploadProfilePhoto(Long employeeId, MultipartFile file) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            User user = userRepository.findByEmployee(employee)
                    .orElseThrow(() -> new RuntimeException("User not found for Employee ID: " + employeeId));

            user.setPhotoUrl(file.getBytes());
            user.setFileType(file.getContentType());

            return userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        }
    }




}
