package com.pipTracker.ServiceImpl;

import com.pipTracker.CommonUtil.ValidationClass;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Status;
import com.pipTracker.Entity.User;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


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

    @Autowired
    private JavaMailSender mailSender;



    @Override
    public User registerUser(User user, MultipartFile file) {
        try {

            validateUser(user);

            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("User already registered with email: " + user.getEmail());
            }

            // Fetch employee
            Long empId = user.getEmployee().getEmployeeId();
            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));

            // Match employee and user email
            if (!employee.getEmail().equals(user.getEmail())) {
                throw new RuntimeException("User email and Employee email do not match");
            }

            // Match employee and user role
            if (!employee.getRole().equals(user.getRole())) {
                throw new RuntimeException("User Role and Employee Role do not match");
            }

            //  NEW CONDITION: If admin is inactive, user cannot register
            if (employee.getManagerId() != null) {
                Optional<Employee> managerOpt = employeeRepository.findById(employee.getManagerId());
                if (managerOpt.isPresent()) {
                    Employee manager = managerOpt.get();
                    if (manager.getStatus() == Status.INACTIVE) {
                        throw new RuntimeException("User cannot register because admin/manager is inactive.");
                    }

                }
            }

            //  File handling
            if (file != null && !file.isEmpty()) {
                if (file.getSize() > ValidationClass.MAX_FILE_SIZE) {
                    throw new RuntimeException("File size exceeds 100MB limit.");
                }

                String contentType = file.getContentType();
                if (contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png")) {
                    user.setPhotoUrl(file.getBytes());
                    user.setFileType(contentType);
                } else {
                    throw new RuntimeException("Invalid file format. Only JPEG, JPG, PNG allowed.");
                }
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmployee(employee);


            if (employee.getStatus() == Status.INACTIVE) {
                    throw new RuntimeException("Your profile is INACTIVE. Please contact the administrator."); }


            return userRepository.save(user);


        } catch (Exception e) {
            throw new RuntimeException("Error while registering user: " + e.getMessage());
        }
    }




    @Override
    public User loginUser(String email, String password) {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {

                User u = user.get();
                Employee empl = u.getEmployee();
                if (empl != null && empl.getStatus() == Status.INACTIVE) {
                    throw new RuntimeException("Your account is inactive. Please contact admin.");
                }

                // Check password
                if (passwordEncoder.matches(password, u.getPassword())) {

                // Use passwordEncoder.matches to compare raw password with encoded password
                if (passwordEncoder.matches(password, user.get().getPassword())) {

                    if (email != null) {
                        Optional<Employee> opt = employeeRepository.findByEmail(email);
                        if (opt.isPresent()) {
                            Employee emp = opt.get();
                            String toEmail = email;
                            String subject = "No Reply";

                            String body = "Dear " + emp.getName() + "," + "\n\nI hope this message finds you well. " +
                                    "\nYou have logged in on " + LocalDateTime.now() + "." +
                                    "\nIf you have any related queries, feel free to reach out to us." + "\n\n"
                                    + "Best Regards," + "\n" + "HR Team." + "\n\n\nThis is an auto-generated mail.";

                            String body = "Dear " + emp.getName() + "," + "\n\nI hope this message finds you a well. " +
                                    "\nYou have Logged in on " + LocalDateTime.now() + "." + "\nIf you have any related queries feel free to reach out us." + "\n\n"
                                    + "Best Regards," + "\n" + "HR Team." + "\n\n\nThis is auto-generated mail.";


                            emailSenderService.sendEmail(toEmail, subject, body);
                        }
                    }
                    return u;
                } else {
                    throw new RuntimeException("Invalid Password,Please Enter valid password");
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
            logger.info("Error while fetching user by name: " + e.getMessage());
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
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
        mailSender.send(message);

        return "OTP sent to your email!";
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (user.getOtp() != null && user.getOtp().equals(otp)
                && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }


    @Override
    public String resetPassword(String email, String newPassword, String confirmPassword) {


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match!");
        }


        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        ValidUserPassword(newPassword);
        userRepository.save(user);


            return "Password updated successfully!";


        return "Password updated successfully!";



    }


    /* @Override
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
 }*/

    @Override
    public User uploadProfilePhoto(Long employeeId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty!");
        }

        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));


            User user = userRepository.findByEmployee(employee)
                    .orElseThrow(() -> new RuntimeException("User not found for Employee ID: " + employeeId));
            if (file.getSize() > ValidationClass.MAX_FILE_SIZE) {
                throw new RuntimeException("File size exceeds 100MB limit.");
            }

            String contentType = file.getContentType();
            if (!("image/jpeg".equals(contentType) ||
                    "image/jpg".equals(contentType) ||
                    "image/png".equals(contentType))) {
                throw new RuntimeException("Invalid file format. Only JPEG, JPG, PNG allowed.");
            }

            user.setPhotoUrl(file.getBytes());
            user.setFileType(contentType);
            user.setFileSize(file.getSize());

            return userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        }
    }

    @Override
    public byte[] getUserPhoto(Long employeeId) {
        User user = userRepository.findByEmployeeEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("User not found with employeeId: " + employeeId));

        if (user.getPhotoUrl() == null) {
            throw new RuntimeException("No photo uploaded for user " + employeeId);
        }


        return user.getPhotoUrl();
    }


    private void validateUser(User user) {

        if (user.getName() == null ||
                !ValidationClass.NAME_PATTERN.matcher(user.getName()).matches()) {
            throw new IllegalArgumentException("Invalid name format");
        }

        if (user.getEmail() == null ||
                !ValidationClass.EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getDepartment() != null &&
                !ValidationClass.DEPARTMENT_PATTERN.matcher(user.getDepartment()).matches()) {
            throw new IllegalArgumentException("Invalid department format");
        }

        if (user.getDesignation() != null &&
                !ValidationClass.DESIGNATION_PATTERN.matcher(user.getDesignation()).matches()) {
            throw new IllegalArgumentException("Invalid designation format");
        }

        if (user.getSkills() != null &&
                !ValidationClass.SKILLS_PATTERN.matcher(user.getSkills()).matches()) {
            throw new IllegalArgumentException("Invalid skills format");
        }



        if (user.getIsregistered() != null &&
                !ValidationClass.ISREGISTERED_PATTERN.matcher(user.getIsregistered()).matches()) {
            throw new IllegalArgumentException("Invalid registration status (Allowed: Yes, No)");
        }


        if (user.getFileType() != null &&
                !ValidationClass.FILE_TYPE_PATTERN.matcher(user.getFileType()).matches()) {
            throw new IllegalArgumentException("Invalid photo file type");
        }

        if (user.getOtp() != null &&
                !ValidationClass.OTP_PATTERN.matcher(user.getOtp()).matches()) {
            throw new IllegalArgumentException("Invalid OTP format");
        }


    }

    private void ValidUserPassword(String Password) {

        if (Password == null ||
                !ValidationClass.PASSWORD_PATTERN.matcher(Password).matches()) {
            throw new IllegalArgumentException("Invalid password format (must contain uppercase, lowercase, digit & special char)");
        }

    }
}
