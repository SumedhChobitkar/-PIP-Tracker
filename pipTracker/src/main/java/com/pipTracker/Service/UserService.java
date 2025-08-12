package com.pipTracker.Service;

import com.pipTracker.Entity.User;

import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    User loginUser(String email,String password);
    User getUserByEmployeeId(Long employeeId);
    Optional<User> getUserByName(String name);


}
