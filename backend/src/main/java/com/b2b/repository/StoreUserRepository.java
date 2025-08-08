package com.b2b.repository;

import com.b2b.entity.Store;
import com.b2b.entity.StoreUser;
import com.b2b.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreUserRepository extends JpaRepository<StoreUser, Long> {
    
    List<StoreUser> findByStore(Store store);
    
    List<StoreUser> findByUser(User user);
    
    Page<StoreUser> findByStore(Store store, Pageable pageable);
    
    Page<StoreUser> findByUser(User user, Pageable pageable);
    
    Optional<StoreUser> findByStoreAndUser(Store store, User user);
    
    boolean existsByStoreAndUser(Store store, User user);
    
    List<StoreUser> findByStoreAndRole(Store store, StoreUser.Role role);
    
    @Query("SELECT su FROM StoreUser su WHERE su.store.id = :storeId")
    List<StoreUser> findByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT su FROM StoreUser su WHERE su.user.id = :userId")
    List<StoreUser> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(su) FROM StoreUser su WHERE su.store.id = :storeId")
    long countByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT COUNT(su) FROM StoreUser su WHERE su.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    void deleteByStoreAndUser(Store store, User user);
}