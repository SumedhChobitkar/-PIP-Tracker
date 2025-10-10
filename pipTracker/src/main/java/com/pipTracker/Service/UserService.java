package com.pipTracker.Service;

import com.pipTracker.Entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    User registerUser(User user, MultipartFile file);
    User loginUser(String email,String password);
    User getUserByEmployeeId(Long employeeId);
    Optional<User> getUserByName(String name);
    String updatePassword(Long employeeId, String newPassword);
    User uploadProfilePhoto(Long employeeId, MultipartFile file);
    byte[] getUserPhoto(Long employeeId);
    String forgotPassword(String email);
    boolean verifyOtp(String email, String otp);
    String resetPassword(String email, String newPassword, String confirmPassword);


}
