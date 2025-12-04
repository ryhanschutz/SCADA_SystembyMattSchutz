package com.mattschutz.scada.repository;

import com.mattschutz.scada.entity.User;
import com.mattschutz.scada.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByActive(Boolean active);
    
    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.fullName")
    List<User> findAllActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true AND u.role = :role")
    long countActiveUsersByRole(@Param("role") UserRole role);
}
