package com.b2b.controller;

import com.b2b.dto.request.ForgotPasswordRequest;
import com.b2b.dto.request.LoginRequest;
import com.b2b.dto.request.ResetPasswordRequest;
import com.b2b.dto.request.UserRegistrationRequest;
import com.b2b.dto.response.ApiResponse;
import com.b2b.dto.response.AuthResponse;
import com.b2b.dto.response.UserResponse;
import com.b2b.service.AuthService;
import com.b2b.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = authService.registerUser(registrationRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", userResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse userResponse = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String message = passwordResetService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String message = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        
        if (message.equals("密碼已成功重置。")) {
            return ResponseEntity.ok(ApiResponse.success(message, message));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(message));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ApiResponse<String>> validateResetToken(@RequestParam String token) {
        String message = passwordResetService.validateToken(token);
        
        if (message.equals("令牌有效。")) {
            return ResponseEntity.ok(ApiResponse.success(message, message));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(message));
        }
    }
}