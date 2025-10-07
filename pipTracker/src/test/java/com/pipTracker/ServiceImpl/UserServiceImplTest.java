package com.pipTracker.ServiceImpl;

import com.pipTracker.CommonUtil.ValidationClass;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Role;
import com.pipTracker.Entity.User;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Shilpa");
        employee.setEmail("shilpa@test.com");
        employee.setRole(Role.MANAGER);


        user = new User();
        user.setName("Shilpa");
        user.setEmail("shilpa@test.com");
        user.setRole(Role.MANAGER);
        user.setEmployee(employee);
        user.setPassword("Password@123");
    }

    @Test
    void testRegisterUser_Success() throws IOException {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(user, null);

        assertNotNull(savedUser);
        assertEquals("Shilpa", savedUser.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(user, null);
        });

        assertTrue(exception.getMessage().contains("User already registered"));
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", user.getPassword())).thenReturn(true);

        User loggedInUser = userService.loginUser(user.getEmail(), "Password@123");

        assertNotNull(loggedInUser);
        assertEquals("Shilpa", loggedInUser.getName());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(user.getEmail(), "wrongPassword");
        });

        assertEquals("Invalid Password", exception.getMessage());
    }

    @Test
    void testGetUserByEmployeeId_Found() {
        when(userRepository.findByEmployeeEmployeeId(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByEmployeeId(1L);
        assertNotNull(foundUser);
        assertEquals("Shilpa", foundUser.getName());
    }

    @Test
    void testGetUserByEmployeeId_NotFound() {
        when(userRepository.findByEmployeeEmployeeId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByEmployeeId(1L);
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testUpdatePassword_Success() {
        user.setPassword("oldPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPassword", "oldPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        String result = userService.updatePassword(1L, "newPassword");
        assertEquals("Password updated successfully!", result);
        verify(userRepository).save(user);
    }

    @Test
    void testForgotPassword_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        String result = userService.forgotPassword(user.getEmail());
        assertEquals("OTP sent to your email!", result);
        assertNotNull(user.getOtp());
    }

    @Test
    void testVerifyOtp_Valid() {
        user.setOtp("123456");
        user.setOtpExpiry(java.time.LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = userService.verifyOtp(user.getEmail(), "123456");
        assertTrue(result);
    }

    @Test
    void testVerifyOtp_Invalid() {
        user.setOtp("123456");
        user.setOtpExpiry(java.time.LocalDateTime.now().minusMinutes(5));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = userService.verifyOtp(user.getEmail(), "123456");
        assertFalse(result);
    }

    @Test
    void testResetPassword_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("encodedNewPassword");

        String result = userService.resetPassword(user.getEmail(), "NewPassword@123", "NewPassword@123");
        assertEquals("Password updated successfully!", result);
        verify(userRepository).save(user);
    }


    @Test
    void testRegisterUser_InvalidEmployee() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty()); // Employee not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(user, null);
        });

        assertTrue(exception.getMessage().contains("Employee not found"));
    }
    @Test
    void testLoginUser_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(user.getEmail(), "Password@123");
        });

        assertEquals("User not found", exception.getMessage());
    }
//
@Test
void testUpdatePassword_UserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    String result = userService.updatePassword(1L, "newPassword");

    assertTrue(result.contains("User not found"));
}

    @Test
    void testResetPassword_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.resetPassword("wrong@test.com", "newPass@123", "newPass@123");
        });

        assertFalse(exception.getMessage().contains("User not found"));
    }
    @Test
    void testResetPassword_PasswordMismatch() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.resetPassword(user.getEmail(), "newPass@123", "differentPass@123");
        });

        assertEquals("New password and confirm password do not match!", exception.getMessage());
    }
    @Test
    void testVerifyOtp_UserNotFound() {
        when(userRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.verifyOtp("wrong@test.com", "123456");
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }
    @Test
    void testForgotPassword_UserNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.forgotPassword("unknown@test.com");
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

}
