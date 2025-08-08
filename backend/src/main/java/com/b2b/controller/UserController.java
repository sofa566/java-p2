package com.b2b.controller;

import com.b2b.dto.request.UserUpdateRequest;
import com.b2b.dto.response.ApiResponse;
import com.b2b.dto.response.UserResponse;
import com.b2b.entity.User;
import com.b2b.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> users;
        if (search != null && !search.trim().isEmpty()) {
            if (active != null) {
                users = userService.searchActiveUsers(search, pageable);
            } else {
                users = userService.searchUsers(search, pageable);
            }
        } else if (active != null && active) {
            users = userService.getActiveUsers(pageable);
        } else {
            users = userService.getAllUsers(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).username == authentication.name")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByRole(
            @PathVariable User.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).username == authentication.name")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        
        UserResponse updatedUser = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Username availability check", !exists));
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Email availability check", !exists));
    }
}