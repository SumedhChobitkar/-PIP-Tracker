package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Role;
import com.pipTracker.Entity.User;
import com.pipTracker.Service.UserService;
import com.pipTracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private Employee mockEmployee;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Setup mock employee
        mockEmployee = new Employee();
        mockEmployee.setEmployeeId(1L);

        // Setup mock user
        mockUser = new User();
        mockUser.setEmail("hr@gmail.com");
        mockUser.setPassword("encodedPassword"); // pretend encoded
        mockUser.setEmployee(mockEmployee);
        mockUser.setRole(Role.HR);

        // Mock userService login
        when(userService.loginUser(eq("hr@gmail.com"), eq("hr@123")))
                .thenReturn(mockUser);

        // Mock JWT generation
        when(jwtService.generateToken("hr@gmail.com", "HR"))
                .thenReturn("mock-jwt-token");

        // Mock password encoding if needed
        when(passwordEncoder.encode(any(String.class)))
                .thenReturn("encodedPassword");
    }

    @Test
    void testLogin_Success() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .param("email", "hr@gmail.com")
                        .param("password", "hr@123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login Successful"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userService.registerUser(any(User.class), any()))
                .thenReturn(mockUser);

        String userJson = objectMapper.writeValueAsString(mockUser);

        mockMvc.perform(multipart("/api/users/register")
                        .file(new MockMultipartFile("file", new byte[0]))
                        .param("userData", userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("hr@gmail.com"));
    }

    @Test
    void testGetUserByEmployeeId() throws Exception {
        when(userService.getUserByEmployeeId(1L))
                .thenReturn(mockUser);

        mockMvc.perform(get("/api/users/employeeId")
                        .param("employeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hr@gmail.com"));
    }

    @Test
    void testGetUserByName() throws Exception {
        when(userService.getUserByName("testName"))
                .thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/getEmployeeByName/testName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hr@gmail.com"));
    }

    @Test
    void testUpdatePassword() throws Exception {
        when(userService.updatePassword(1L, "newPass"))
                .thenReturn("Password updated successfully");

        mockMvc.perform(put("/api/users/updatePassword/1")
                        .param("newPassword", "newPass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }

    @Test
    void testForgotPassword() throws Exception {
        when(userService.forgotPassword("hr@gmail.com"))
                .thenReturn("OTP sent successfully");

        mockMvc.perform(post("/api/users/forgot-password")
                        .param("email", "hr@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent successfully"));
    }

    @Test
    void testVerifyOtp() throws Exception {
        when(userService.verifyOtp("hr@gmail.com", "123456"))
                .thenReturn(true);

        mockMvc.perform(post("/api/users/verify-otp")
                        .param("email", "hr@gmail.com")
                        .param("otp", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP verified! You can reset your password."));
    }

    @Test
    void testResetPassword() throws Exception {
        when(userService.resetPassword("hr@gmail.com", "newPass", "newPass"))
                .thenReturn("Password reset successfully");

        mockMvc.perform(post("/api/users/reset-password")
                        .param("email", "hr@gmail.com")
                        .param("newPassword", "newPass")
                        .param("confirmPassword", "newPass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));
    }

    @Test
    void testUploadProfilePhoto() throws Exception {
        when(userService.uploadProfilePhoto(eq(1L), any()))
                .thenReturn(mockUser);

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "test".getBytes());

        mockMvc.perform(multipart("/api/users/uploadPhoto/1")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hr@gmail.com"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        when(userService.loginUser("wrong@gmail.com", "wrongPass"))
                .thenThrow(new RuntimeException("Invalid email or password"));

        mockMvc.perform(post("/api/users/login")
                        .param("email", "wrong@gmail.com")
                        .param("password", "wrongPass"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void testRegister_InvalidJson() throws Exception {
        String invalidJson = "{invalid: json}";

        mockMvc.perform(multipart("/api/users/register")
                        .param("userData", invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error")));
    }

    @Test
    void testGetUserByEmployeeId_NotFound() throws Exception {
        when(userService.getUserByEmployeeId(99L))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/employeeId")
                        .param("employeeId", "99"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
    }

    @Test
    void testGetUserByName_NotFound() throws Exception {
        when(userService.getUserByName("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/getEmployeeByName/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testUpdatePassword_Error() throws Exception {
        when(userService.updatePassword(1L, "newPass")).thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/users/updatePassword/1")
                        .param("newPassword", "newPass"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error while updating password")));
    }

    @Test
    void testForgotPassword_Error() throws Exception {
        when(userService.forgotPassword("unknown@gmail.com")).thenThrow(new RuntimeException("Email not found"));

        mockMvc.perform(post("/api/users/forgot-password")
                        .param("email", "unknown@gmail.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error")));
    }

    @Test
    void testVerifyOtp_Invalid() throws Exception {
        when(userService.verifyOtp("hr@gmail.com", "000000")).thenReturn(false);

        mockMvc.perform(post("/api/users/verify-otp")
                        .param("email", "hr@gmail.com")
                        .param("otp", "000000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired OTP."));
    }

    @Test
    void testResetPassword_Error() throws Exception {
        when(userService.resetPassword("hr@gmail.com", "pass1", "pass2"))
                .thenThrow(new RuntimeException("Passwords do not match"));

        mockMvc.perform(post("/api/users/reset-password")
                        .param("email", "hr@gmail.com")
                        .param("newPassword", "pass1")
                        .param("confirmPassword", "pass2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error")));
    }

    @Test
    void testUploadProfilePhoto_Error() throws Exception {
        when(userService.uploadProfilePhoto(eq(1L), any()))
                .thenThrow(new RuntimeException("File upload failed"));

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "test".getBytes());

        mockMvc.perform(multipart("/api/users/uploadPhoto/1").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error while uploading photo")));
    }
}
