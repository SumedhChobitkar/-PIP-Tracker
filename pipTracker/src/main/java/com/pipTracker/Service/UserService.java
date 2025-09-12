package com.pipTracker.Service;

import com.pipTracker.Entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    User registerUser(User user, MultipartFile file);
    User loginUser(String email,String password);
    User getUserByEmployeeId(Long employeeId);
    Optional<User> getUserByName(String name);
    public String updatePassword(Long employeeId, String newPassword);
    User uploadProfilePhoto(Long employeeId, MultipartFile file);



}
