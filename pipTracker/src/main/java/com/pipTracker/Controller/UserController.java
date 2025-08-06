package com.pipTracker.Controller;


import com.pipTracker.Entity.User;
import com.pipTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        User user = userService.loginUser(email, password);
        return new ResponseEntity<>("Login Successful! Welcome " + user.getName(), HttpStatus.OK);
    }

    @GetMapping("/employeeId")
    public ResponseEntity<User> getUserByEmployeeId(@RequestParam Long employeeId) {
        User user = userService.getUserByEmployeeId(employeeId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }





}
