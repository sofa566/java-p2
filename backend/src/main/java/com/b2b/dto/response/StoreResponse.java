package com.b2b.dto.response;

import com.b2b.entity.Store;

import java.time.LocalDateTime;

public class StoreResponse {
    
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phone;
    private String email;
    private UserResponse manager;
    private Store.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StoreResponse() {}

    public StoreResponse(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.description = store.getDescription();
        this.address = store.getAddress();
        this.city = store.getCity();
        this.state = store.getState();
        this.zipCode = store.getZipCode();
        this.country = store.getCountry();
        this.phone = store.getPhone();
        this.email = store.getEmail();
        this.manager = store.getManager() != null ? new UserResponse(store.getManager()) : null;
        this.status = store.getStatus();
        this.createdAt = store.getCreatedAt();
        this.updatedAt = store.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserResponse getManager() {
        return manager;
    }

    public void setManager(UserResponse manager) {
        this.manager = manager;
    }

    public Store.Status getStatus() {
        return status;
    }

    public void setStatus(Store.Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}