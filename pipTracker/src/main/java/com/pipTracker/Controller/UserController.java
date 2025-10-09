/*package com.pipTracker.Controller;


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
 */

/*package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.User;
import com.pipTracker.Service.UserService;
import com.pipTracker.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "User APIs",
        description = "Operations related to User registration, login, password management, and profile photo upload"
)
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Register User",
            description = "Registers a new user with optional profile photo upload.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/register"
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Failed to register user")
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

    @Operation(
            summary = "User Login",
            description = "Logs in a user and returns JWT token.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/login"
    )
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.loginUser(email, password);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);
            response.put("id", user.getEmployee().getEmployeeId());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
            summary = "Get User by Employee ID",
            description = "Fetches the user details using employee ID.\n\n" +
                    "Eg: GET http://localhost:8080/api/users/employeeId?employeeId={employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "User fetched successfully")
    @ApiResponse(responseCode = "500", description = "Error while fetching user")
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

    @Operation(
            summary = "Get User by Name",
            description = "Fetches the user details using their name.\n\n" +
                    "Eg: GET http://localhost:8080/api/users/getEmployeeByName/{name}"
    )
    @ApiResponse(responseCode = "200", description = "User fetched successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
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

    @Operation(
            summary = "Update User Password",
            description = "Updates password for a user.\n\n" +
                    "Eg: PUT http://localhost:8080/api/users/updatePassword/{employeeId}?newPassword={newPassword}"
    )
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @PutMapping("/updatePassword/{employeeId}")
    public ResponseEntity<String> updatePassword(@PathVariable Long employeeId, @RequestParam String newPassword) {
        try {
            String result = userService.updatePassword(employeeId, newPassword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok("Error while updating password: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Forgot Password",
            description = "Generates OTP for password reset.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/forgot-password"
    )
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    @ApiResponse(responseCode = "400", description = "Failed to send OTP")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String result = userService.forgotPassword(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Verify OTP",
            description = "Verifies OTP for password reset.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/verify-otp"
    )
    @ApiResponse(responseCode = "200", description = "OTP verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
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

    @Operation(
            summary = "Reset Password",
            description = "Resets password after OTP verification.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/reset-password"
    )
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    @ApiResponse(responseCode = "400", description = "Password reset failed")
    @PostMapping("/reset-password")
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

    @Operation(
            summary = "Upload Profile Photo",
            description = "Uploads a profile photo for a user.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/uploadPhoto/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Photo uploaded successfully")
    @ApiResponse(responseCode = "500", description = "Error while uploading photo")
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

}*/
package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.User;
import com.pipTracker.Service.UserService;
import com.pipTracker.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "User APIs",
        description = "Operations related to User registration, login, password management, and profile photo upload"
)
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Register User",
            description = "Registers a new user with optional profile photo upload.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/register"
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Failed to register user")
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestPart("userData") String userData,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(userData, User.class);
            User savedUser = userService.registerUser(user, file);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "User Login",
            description = "Logs in a user and returns JWT token.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/login"
    )
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.loginUser(email, password);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);
            response.put("id", user.getEmployee().getEmployeeId());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
            summary = "Get User by Employee ID",
            description = "Fetches the user details using employee ID.\n\n" +
                    "Eg: GET http://localhost:8080/api/users/employeeId?employeeId={employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "User fetched successfully")
    @ApiResponse(responseCode = "500", description = "Error while fetching user")
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

    @Operation(
            summary = "Get User by Name",
            description = "Fetches the user details using their name.\n\n" +
                    "Eg: GET http://localhost:8080/api/users/getEmployeeByName/{name}"
    )
    @ApiResponse(responseCode = "200", description = "User fetched successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/getEmployeeByName/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        try {
            Optional<User> user = userService.getUserByName(name);
            return user.<ResponseEntity<?>>map(value ->
                            new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>("Error while fetching user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Update User Password",
            description = "Updates password for a user.\n\n" +
                    "Eg: PUT http://localhost:8080/api/users/updatePassword/{employeeId}?newPassword={newPassword}"
    )
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @PutMapping("/updatePassword/{employeeId}")
    public ResponseEntity<String> updatePassword(@PathVariable Long employeeId, @RequestParam String newPassword) {
        try {
            String result = userService.updatePassword(employeeId, newPassword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok("Error while updating password: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Forgot Password",
            description = "Generates OTP for password reset.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/forgot-password"
    )
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    @ApiResponse(responseCode = "400", description = "Failed to send OTP")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String result = userService.forgotPassword(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Verify OTP",
            description = "Verifies OTP for password reset.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/verify-otp"
    )
    @ApiResponse(responseCode = "200", description = "OTP verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
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

    @Operation(
            summary = "Reset Password",
            description = "Resets password after OTP verification.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/reset-password"
    )
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    @ApiResponse(responseCode = "400", description = "Password reset failed")
    @PostMapping("/reset-password")
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

    @Operation(
            summary = "Upload Profile Photo",
            description = "Uploads a profile photo for a user.\n\n" +
                    "Eg: POST http://localhost:8080/api/users/uploadPhoto/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Photo uploaded successfully")
    @ApiResponse(responseCode = "500", description = "Error while uploading photo")
    @PostMapping(value = "/uploadPhoto/{employeeId}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file) {
        try {
            User updatedUser = userService.uploadProfilePhoto(employeeId, file);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while uploading photo: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Get Profile Photo",
            description = "Fetches the profile photo for a user.\n\n" +
                    "Eg: GET http://localhost:8080/api/users/getPhoto/{employeeId}"
    )
    @ApiResponse(responseCode = "200", description = "Photo fetched successfully")
    @ApiResponse(responseCode = "404", description = "Photo not found")
    @GetMapping("/getPhoto/{employeeId}")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable Long employeeId) {
        try {
            User user = userService.getUserByEmployeeId(employeeId);
            byte[] imageBytes = userService.getUserPhoto(employeeId);

            return ResponseEntity.ok()
                    .header("Content-Type", user.getFileType())
                    .body(imageBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

