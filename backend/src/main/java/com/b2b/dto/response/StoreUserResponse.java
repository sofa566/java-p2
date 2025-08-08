package com.b2b.dto.response;

import com.b2b.entity.StoreUser;

import java.time.LocalDateTime;

public class StoreUserResponse {
    
    private Long id;
    private StoreResponse store;
    private UserResponse user;
    private StoreUser.Role role;
    private LocalDateTime createdAt;

    public StoreUserResponse() {}

    public StoreUserResponse(StoreUser storeUser) {
        this.id = storeUser.getId();
        this.store = storeUser.getStore() != null ? new StoreResponse(storeUser.getStore()) : null;
        this.user = storeUser.getUser() != null ? new UserResponse(storeUser.getUser()) : null;
        this.role = storeUser.getRole();
        this.createdAt = storeUser.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoreResponse getStore() {
        return store;
    }

    public void setStore(StoreResponse store) {
        this.store = store;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public StoreUser.Role getRole() {
        return role;
    }

    public void setRole(StoreUser.Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}