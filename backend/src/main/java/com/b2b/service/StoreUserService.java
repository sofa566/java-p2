package com.b2b.service;

import com.b2b.entity.Store;
import com.b2b.entity.StoreUser;
import com.b2b.entity.User;
import com.b2b.exception.ResourceAlreadyExistsException;
import com.b2b.exception.ResourceNotFoundException;
import com.b2b.repository.StoreRepository;
import com.b2b.repository.StoreUserRepository;
import com.b2b.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StoreUserService {

    private final StoreUserRepository storeUserRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Autowired
    public StoreUserService(StoreUserRepository storeUserRepository,
                           StoreRepository storeRepository,
                           UserRepository userRepository) {
        this.storeUserRepository = storeUserRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public StoreUser addUserToStore(Long storeId, Long userId, StoreUser.Role role) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (storeUserRepository.existsByStoreAndUser(store, user)) {
            throw new ResourceAlreadyExistsException("User is already associated with this store");
        }

        StoreUser storeUser = new StoreUser();
        storeUser.setStore(store);
        storeUser.setUser(user);
        storeUser.setRole(role);

        return storeUserRepository.save(storeUser);
    }

    @Transactional(readOnly = true)
    public List<StoreUser> getUsersByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        return storeUserRepository.findByStore(store);
    }

    @Transactional(readOnly = true)
    public Page<StoreUser> getUsersByStore(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        return storeUserRepository.findByStore(store, pageable);
    }

    @Transactional(readOnly = true)
    public List<StoreUser> getStoresByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return storeUserRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<StoreUser> getStoresByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return storeUserRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public List<StoreUser> getUsersByStoreAndRole(Long storeId, StoreUser.Role role) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        return storeUserRepository.findByStoreAndRole(store, role);
    }

    public StoreUser updateUserRole(Long storeId, Long userId, StoreUser.Role role) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        StoreUser storeUser = storeUserRepository.findByStoreAndUser(store, user)
            .orElseThrow(() -> new ResourceNotFoundException("User is not associated with this store"));
        
        storeUser.setRole(role);
        return storeUserRepository.save(storeUser);
    }

    public void removeUserFromStore(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!storeUserRepository.existsByStoreAndUser(store, user)) {
            throw new ResourceNotFoundException("User is not associated with this store");
        }
        
        storeUserRepository.deleteByStoreAndUser(store, user);
    }

    @Transactional(readOnly = true)
    public long getUserCountByStore(Long storeId) {
        return storeUserRepository.countByStoreId(storeId);
    }

    @Transactional(readOnly = true)
    public long getStoreCountByUser(Long userId) {
        return storeUserRepository.countByUserId(userId);
    }
}