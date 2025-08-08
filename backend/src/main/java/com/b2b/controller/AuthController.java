package com.b2b.controller;

import com.b2b.dto.request.LoginRequest;
import com.b2b.dto.request.UserRegistrationRequest;
import com.b2b.dto.response.ApiResponse;
import com.b2b.dto.response.AuthResponse;
import com.b2b.dto.response.UserResponse;
import com.b2b.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
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
}