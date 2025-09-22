package com.pipTracker.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> register(
            @RequestParam("userData") String userData,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(userData, User.class);
            User savedUser = userService.registerUser(user, file);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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
            response.put("id",user.getEmployee().getEmployeeId());
           // response.put("username", user.getName());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/employeeId")
    public ResponseEntity<?> getUserByEmployeeId(@RequestParam Long employeeId) {
        try {
            User user = userService.getUserByEmployeeId(employeeId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while fetching user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getEmployeeByName/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        try {
            Optional<User> user = userService.getUserByName(name);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error while fetching user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/updatePassword/{employeeId}")
    public ResponseEntity<String> updatePassword(@PathVariable Long employeeId, @RequestParam String newPassword) {
        try {
            String result = userService.updatePassword(employeeId, newPassword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok("Error while updating password: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String result = userService.forgotPassword(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean isValid = userService.verifyOtp(email, otp);
            if (isValid) {
                return ResponseEntity.ok("OTP verified! You can reset your password.");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired OTP.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping ("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        try {
            String result = userService.resetPassword(email, newPassword, confirmPassword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



    @PostMapping("/uploadPhoto/{employeeId}")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file) {
        try {
            User updatedUser = userService.uploadProfilePhoto(employeeId, file);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while uploading photo: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






}