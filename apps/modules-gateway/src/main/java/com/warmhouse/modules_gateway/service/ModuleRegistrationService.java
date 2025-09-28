package com.warmhouse.modules_gateway.service;

import com.warmhouse.modules_gateway.model.ModuleRegistration;
import com.warmhouse.modules_gateway.model.dto.ModuleRegistrationRequest;
import com.warmhouse.modules_gateway.model.dto.ModuleRegistrationResponse;
import com.warmhouse.modules_gateway.repository.ModuleRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleRegistrationService {
    
    private final ModuleRegistrationRepository repository;
    
    @Transactional
    public ModuleRegistrationResponse registerModule(ModuleRegistrationRequest request) {
        try {
            // Validate module ID format (should be factory-assigned)
            validateModuleId(request.getModuleId());
            
            // Check if module is already registered
            Optional<ModuleRegistration> existing = repository.findByModuleIdAndHomeId(
                request.getModuleId(), 
                request.getHomeId()
            );
            
            if (existing.isPresent()) {
                // Update existing registration
                ModuleRegistration registration = existing.get();
                registration.setBaseUrl(request.getBaseUrl());
                registration.setStatus("ACTIVE");
                registration.setLastHeartbeat(LocalDateTime.now());
                registration.setDescription(request.getDescription());
                
                repository.save(registration);
                
                log.info("Updated registration for module {} in home {}", 
                    request.getModuleId(), request.getHomeId());
                
                return new ModuleRegistrationResponse(true, "Module updated successfully", request.getModuleId());
            } else {
                // Create new registration
                ModuleRegistration registration = new ModuleRegistration();
                registration.setModuleId(request.getModuleId());
                registration.setModuleType(request.getModuleType());
                registration.setHomeId(request.getHomeId());
                registration.setBaseUrl(request.getBaseUrl());
                registration.setStatus("ACTIVE");
                registration.setRegisteredAt(LocalDateTime.now());
                registration.setLastHeartbeat(LocalDateTime.now());
                registration.setDescription(request.getDescription());
                
                repository.save(registration);
                
                log.info("Registered new module {} of type {} in home {}", 
                    request.getModuleId(), request.getModuleType(), request.getHomeId());
                
                return new ModuleRegistrationResponse(true, "Module registered successfully", request.getModuleId());
            }
        } catch (Exception e) {
            log.error("Error registering module {}: {}", request.getModuleId(), e.getMessage(), e);
            return new ModuleRegistrationResponse(false, "Registration failed: " + e.getMessage(), request.getModuleId());
        }
    }
    
    @Transactional
    public ModuleRegistrationResponse updateHeartbeat(String moduleId, String homeId) {
        try {
            Optional<ModuleRegistration> registration = repository.findByModuleIdAndHomeId(moduleId, homeId);
            
            if (registration.isPresent()) {
                ModuleRegistration reg = registration.get();
                reg.setLastHeartbeat(LocalDateTime.now());
                reg.setStatus("ACTIVE");
                repository.save(reg);
                
                log.debug("Updated heartbeat for module {} in home {}", moduleId, homeId);
                return new ModuleRegistrationResponse(true, "Heartbeat updated", moduleId);
            } else {
                log.warn("Heartbeat update failed: module {} not found in home {}", moduleId, homeId);
                return new ModuleRegistrationResponse(false, "Module not found", moduleId);
            }
        } catch (Exception e) {
            log.error("Error updating heartbeat for module {}: {}", moduleId, e.getMessage(), e);
            return new ModuleRegistrationResponse(false, "Heartbeat update failed: " + e.getMessage(), moduleId);
        }
    }
    
    public List<ModuleRegistration> getModulesByHome(String homeId) {
        return repository.findByHomeId(homeId);
    }
    
    public List<ModuleRegistration> getModulesByType(String moduleType) {
        return repository.findByModuleType(moduleType);
    }
    
    public List<ModuleRegistration> getAllModules() {
        return repository.findAll();
    }
    
    @Transactional
    public void unregisterModule(String moduleId, String homeId) {
        try {
            Optional<ModuleRegistration> registration = repository.findByModuleIdAndHomeId(moduleId, homeId);
            if (registration.isPresent()) {
                repository.delete(registration.get());
                log.info("Unregistered module {} from home {}", moduleId, homeId);
            }
        } catch (Exception e) {
            log.error("Error unregistering module {}: {}", moduleId, e.getMessage(), e);
        }
    }
    
    private void validateModuleId(String moduleId) {
        if (moduleId == null || moduleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Module ID cannot be null or empty");
        }
        
        // Validate that module ID follows factory-assigned format
        // Expected format: {module-type}-module-factory-{number}
        if (!moduleId.matches("^[a-zA-Z0-9-]+-module-factory-\\d+$")) {
            log.warn("Module ID '{}' does not follow expected factory format", moduleId);
            // We don't throw exception here, just log warning to allow flexibility
        }
        
        // Check for minimum length
        if (moduleId.length() < 10) {
            throw new IllegalArgumentException("Module ID must be at least 10 characters long");
        }
        
        log.debug("Module ID validation passed for: {}", moduleId);
    }
}
