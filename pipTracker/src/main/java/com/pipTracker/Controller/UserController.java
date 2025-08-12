package com.pipTracker.Controller;


import com.pipTracker.Entity.User;
import com.pipTracker.Service.EmployeeService;
import com.pipTracker.Service.UserService;
import com.pipTracker.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private  JwtService jwtUtil;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    private EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    //    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
//        User user = userService.loginUser(email, password);
//        return new ResponseEntity<>("Login Successful! Welcome " + user.getName(), HttpStatus.OK);
//    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.loginUser(email, password);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);
           // response.put("username", user.getName());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping("/employeeId")
    public ResponseEntity<User> getUserByEmployeeId(@RequestParam Long employeeId) {
        User user = userService.getUserByEmployeeId(employeeId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getEmployeeByName/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        Optional<User> user = userService.getUserByName(name);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }



}