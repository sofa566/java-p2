package com.b2b.controller;

import com.b2b.dto.request.StoreCreateRequest;
import com.b2b.dto.request.StoreUpdateRequest;
import com.b2b.dto.response.ApiResponse;
import com.b2b.dto.response.StoreResponse;
import com.b2b.entity.Store;
import com.b2b.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(@Valid @RequestBody StoreCreateRequest createRequest) {
        StoreResponse store = storeService.createStore(createRequest);
        return ResponseEntity.ok(ApiResponse.success("Store created successfully", store));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<StoreResponse>>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Store.Status status) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StoreResponse> stores;
        if (search != null && !search.trim().isEmpty()) {
            if (status != null) {
                stores = storeService.searchStoresByStatus(status, search, pageable);
            } else {
                stores = storeService.searchStores(search, pageable);
            }
        } else if (status != null) {
            stores = storeService.getStoresByStatus(status, pageable);
        } else {
            stores = storeService.getAllStores(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable Long id) {
        StoreResponse store = storeService.getStoreById(id);
        return ResponseEntity.ok(ApiResponse.success(store));
    }

    @GetMapping("/by-manager/{managerId}")
    @PreAuthorize("hasRole('ADMIN') or #managerId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getStoresByManager(@PathVariable Long managerId) {
        List<StoreResponse> stores = storeService.getStoresByManager(managerId);
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @GetMapping("/by-manager/{managerId}/paginated")
    @PreAuthorize("hasRole('ADMIN') or #managerId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Page<StoreResponse>>> getStoresByManagerPaginated(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<StoreResponse> stores = storeService.getStoresByManager(managerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreUpdateRequest updateRequest) {
        
        StoreResponse updatedStore = storeService.updateStore(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Store updated successfully", updatedStore));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> activateStore(@PathVariable Long id) {
        StoreResponse store = storeService.activateStore(id);
        return ResponseEntity.ok(ApiResponse.success("Store activated successfully", store));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> deactivateStore(@PathVariable Long id) {
        StoreResponse store = storeService.deactivateStore(id);
        return ResponseEntity.ok(ApiResponse.success("Store deactivated successfully", store));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok(ApiResponse.success("Store deleted successfully"));
    }

    @GetMapping("/manager/{managerId}/count")
    @PreAuthorize("hasRole('ADMIN') or #managerId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Long>> getStoreCountByManager(@PathVariable Long managerId) {
        long count = storeService.getStoreCountByManager(managerId);
        return ResponseEntity.ok(ApiResponse.success("Store count retrieved", count));
    }
}