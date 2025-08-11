package com.b2b.service;

import com.b2b.dto.request.LoginRequest;
import com.b2b.dto.request.UserRegistrationRequest;
import com.b2b.dto.response.AuthResponse;
import com.b2b.dto.response.UserResponse;
import com.b2b.entity.User;
import com.b2b.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, 
                      UserService userService, 
                      JwtUtils jwtUtils,
                      EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(), 
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();
        UserResponse userResponse = new UserResponse(user);

        return new AuthResponse(jwt, userResponse);
    }

    public UserResponse registerUser(UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = userService.createUser(registrationRequest);
        
        // 發送歡迎郵件
        try {
            emailService.sendWelcomeEmail(
                userResponse.getEmail(), 
                userResponse.getFirstName() + " " + userResponse.getLastName()
            );
        } catch (Exception e) {
            // 記錄錯誤但不影響註冊流程
            System.err.println("發送歡迎郵件失敗: " + e.getMessage());
        }
        
        return userResponse;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return new UserResponse(user);
        }
        throw new IllegalStateException("No authenticated user found");
    }
}