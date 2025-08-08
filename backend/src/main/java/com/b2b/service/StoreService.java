package com.b2b.service;

import com.b2b.dto.request.StoreCreateRequest;
import com.b2b.dto.request.StoreUpdateRequest;
import com.b2b.dto.response.StoreResponse;
import com.b2b.entity.Store;
import com.b2b.entity.User;
import com.b2b.exception.ResourceNotFoundException;
import com.b2b.repository.StoreRepository;
import com.b2b.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public StoreResponse createStore(StoreCreateRequest request) {
        Store store = new Store();
        store.setName(request.getName());
        store.setDescription(request.getDescription());
        store.setAddress(request.getAddress());
        store.setCity(request.getCity());
        store.setState(request.getState());
        store.setZipCode(request.getZipCode());
        store.setCountry(request.getCountry());
        store.setPhone(request.getPhone());
        store.setEmail(request.getEmail());

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
            store.setManager(manager);
        }

        Store savedStore = storeRepository.save(store);
        return new StoreResponse(savedStore);
    }

    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Long id) {
        Store store = storeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        return new StoreResponse(store);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable)
            .map(StoreResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> getStoresByStatus(Store.Status status, Pageable pageable) {
        return storeRepository.findByStatus(status, pageable)
            .map(StoreResponse::new);
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> getStoresByManager(Long managerId) {
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + managerId));
        
        return storeRepository.findByManager(manager)
            .stream()
            .map(StoreResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> getStoresByManager(Long managerId, Pageable pageable) {
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + managerId));
        
        return storeRepository.findByManager(manager, pageable)
            .map(StoreResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> searchStores(String search, Pageable pageable) {
        return storeRepository.findBySearchTerm(search, pageable)
            .map(StoreResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> searchStoresByStatus(Store.Status status, String search, Pageable pageable) {
        return storeRepository.findByStatusAndSearchTerm(status, search, pageable)
            .map(StoreResponse::new);
    }

    public StoreResponse updateStore(Long id, StoreUpdateRequest request) {
        Store store = storeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        if (request.getName() != null) {
            store.setName(request.getName());
        }

        if (request.getDescription() != null) {
            store.setDescription(request.getDescription());
        }

        if (request.getAddress() != null) {
            store.setAddress(request.getAddress());
        }

        if (request.getCity() != null) {
            store.setCity(request.getCity());
        }

        if (request.getState() != null) {
            store.setState(request.getState());
        }

        if (request.getZipCode() != null) {
            store.setZipCode(request.getZipCode());
        }

        if (request.getCountry() != null) {
            store.setCountry(request.getCountry());
        }

        if (request.getPhone() != null) {
            store.setPhone(request.getPhone());
        }

        if (request.getEmail() != null) {
            store.setEmail(request.getEmail());
        }

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
            store.setManager(manager);
        }

        if (request.getStatus() != null) {
            store.setStatus(request.getStatus());
        }

        Store updatedStore = storeRepository.save(store);
        return new StoreResponse(updatedStore);
    }

    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store not found with id: " + id);
        }
        storeRepository.deleteById(id);
    }

    public StoreResponse activateStore(Long id) {
        Store store = storeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        
        store.setStatus(Store.Status.ACTIVE);
        Store updatedStore = storeRepository.save(store);
        return new StoreResponse(updatedStore);
    }

    public StoreResponse deactivateStore(Long id) {
        Store store = storeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        
        store.setStatus(Store.Status.INACTIVE);
        Store updatedStore = storeRepository.save(store);
        return new StoreResponse(updatedStore);
    }

    @Transactional(readOnly = true)
    public long getStoreCountByManager(Long managerId) {
        return storeRepository.countByManagerId(managerId);
    }
}