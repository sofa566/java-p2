package com.b2b.repository;

import com.b2b.entity.Store;
import com.b2b.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    Page<Store> findByStatus(Store.Status status, Pageable pageable);
    
    List<Store> findByManager(User manager);
    
    Page<Store> findByManager(User manager, Pageable pageable);
    
    @Query("SELECT s FROM Store s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.state) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Store> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT s FROM Store s WHERE s.status = :status AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.state) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Store> findByStatusAndSearchTerm(@Param("status") Store.Status status, 
                                          @Param("search") String search, 
                                          Pageable pageable);
    
    List<Store> findByStatusAndManagerId(Store.Status status, Long managerId);
    
    @Query("SELECT COUNT(s) FROM Store s WHERE s.manager.id = :managerId")
    long countByManagerId(@Param("managerId") Long managerId);
}