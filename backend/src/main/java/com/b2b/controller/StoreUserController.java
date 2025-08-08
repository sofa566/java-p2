package com.b2b.controller;

import com.b2b.dto.response.ApiResponse;
import com.b2b.dto.response.StoreUserResponse;
import com.b2b.entity.StoreUser;
import com.b2b.service.StoreUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store-users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StoreUserController {

    private final StoreUserService storeUserService;

    @Autowired
    public StoreUserController(StoreUserService storeUserService) {
        this.storeUserService = storeUserService;
    }

    @PostMapping("/stores/{storeId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<StoreUserResponse>> addUserToStore(
            @PathVariable Long storeId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "EMPLOYEE") StoreUser.Role role) {
        
        StoreUser storeUser = storeUserService.addUserToStore(storeId, userId, role);
        StoreUserResponse response = new StoreUserResponse(storeUser);
        return ResponseEntity.ok(ApiResponse.success("User added to store successfully", response));
    }

    @GetMapping("/stores/{storeId}/users")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<List<StoreUserResponse>>> getUsersByStore(@PathVariable Long storeId) {
        List<StoreUser> storeUsers = storeUserService.getUsersByStore(storeId);
        List<StoreUserResponse> response = storeUsers.stream()
            .map(StoreUserResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stores/{storeId}/users/paginated")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<Page<StoreUserResponse>>> getUsersByStorePaginated(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<StoreUserResponse> response = storeUserService.getUsersByStore(storeId, pageable)
            .map(StoreUserResponse::new);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/{userId}/stores")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<StoreUserResponse>>> getStoresByUser(@PathVariable Long userId) {
        List<StoreUser> storeUsers = storeUserService.getStoresByUser(userId);
        List<StoreUserResponse> response = storeUsers.stream()
            .map(StoreUserResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/{userId}/stores/paginated")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Page<StoreUserResponse>>> getStoresByUserPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<StoreUserResponse> response = storeUserService.getStoresByUser(userId, pageable)
            .map(StoreUserResponse::new);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stores/{storeId}/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<List<StoreUserResponse>>> getUsersByStoreAndRole(
            @PathVariable Long storeId,
            @PathVariable StoreUser.Role role) {
        
        List<StoreUser> storeUsers = storeUserService.getUsersByStoreAndRole(storeId, role);
        List<StoreUserResponse> response = storeUsers.stream()
            .map(StoreUserResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/stores/{storeId}/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<StoreUserResponse>> updateUserRole(
            @PathVariable Long storeId,
            @PathVariable Long userId,
            @RequestParam StoreUser.Role role) {
        
        StoreUser storeUser = storeUserService.updateUserRole(storeId, userId, role);
        StoreUserResponse response = new StoreUserResponse(storeUser);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response));
    }

    @DeleteMapping("/stores/{storeId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<String>> removeUserFromStore(
            @PathVariable Long storeId,
            @PathVariable Long userId) {
        
        storeUserService.removeUserFromStore(storeId, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from store successfully"));
    }

    @GetMapping("/stores/{storeId}/users/count")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STORE_MANAGER') and @storeService.getStoreById(#storeId).manager.id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<Long>> getUserCountByStore(@PathVariable Long storeId) {
        long count = storeUserService.getUserCountByStore(storeId);
        return ResponseEntity.ok(ApiResponse.success("User count retrieved", count));
    }

    @GetMapping("/users/{userId}/stores/count")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Long>> getStoreCountByUser(@PathVariable Long userId) {
        long count = storeUserService.getStoreCountByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Store count retrieved", count));
    }
}