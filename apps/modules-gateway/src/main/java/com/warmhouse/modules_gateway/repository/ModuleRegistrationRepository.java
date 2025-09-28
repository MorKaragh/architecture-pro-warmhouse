package com.warmhouse.modules_gateway.repository;

import com.warmhouse.modules_gateway.model.ModuleRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRegistrationRepository extends JpaRepository<ModuleRegistration, Long> {
    
    Optional<ModuleRegistration> findByModuleIdAndHomeId(String moduleId, String homeId);
    
    List<ModuleRegistration> findByHomeId(String homeId);
    
    List<ModuleRegistration> findByModuleType(String moduleType);
    
    List<ModuleRegistration> findByStatus(String status);
}
