package com.pipTracker.Service;

import com.pipTracker.Entity.User;

public interface UserService {
    User registerUser(User user);
    User loginUser(String email,String password);
    User getUserByEmployeeId(Long employeeId);

}
