package com.warmhouse.modules_gateway.api;

import com.warmhouse.modules_gateway.model.ModuleRegistration;
import com.warmhouse.modules_gateway.model.dto.ModuleRegistrationRequest;
import com.warmhouse.modules_gateway.model.dto.ModuleRegistrationResponse;
import com.warmhouse.modules_gateway.service.ModuleRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Slf4j
public class ModuleRegistrationController {
    
    private final ModuleRegistrationService registrationService;
    
    @PostMapping("/register")
    public ResponseEntity<ModuleRegistrationResponse> registerModule(@RequestBody ModuleRegistrationRequest request) {
        log.info("Received registration request for module {} in home {}", 
            request.getModuleId(), request.getHomeId());
        
        ModuleRegistrationResponse response = registrationService.registerModule(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/{moduleId}/heartbeat")
    public ResponseEntity<ModuleRegistrationResponse> updateHeartbeat(
            @PathVariable String moduleId,
            @RequestParam String homeId) {
        
        log.debug("Received heartbeat from module {} in home {}", moduleId, homeId);
        
        ModuleRegistrationResponse response = registrationService.updateHeartbeat(moduleId, homeId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/home/{homeId}")
    public ResponseEntity<List<ModuleRegistration>> getModulesByHome(@PathVariable String homeId) {
        List<ModuleRegistration> modules = registrationService.getModulesByHome(homeId);
        return ResponseEntity.ok(modules);
    }
    
    @GetMapping("/type/{moduleType}")
    public ResponseEntity<List<ModuleRegistration>> getModulesByType(@PathVariable String moduleType) {
        List<ModuleRegistration> modules = registrationService.getModulesByType(moduleType);
        return ResponseEntity.ok(modules);
    }
    
    @GetMapping
    public ResponseEntity<List<ModuleRegistration>> getAllModules() {
        List<ModuleRegistration> modules = registrationService.getAllModules();
        return ResponseEntity.ok(modules);
    }
    
    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> unregisterModule(
            @PathVariable String moduleId,
            @RequestParam String homeId) {
        
        registrationService.unregisterModule(moduleId, homeId);
        return ResponseEntity.ok().build();
    }
}
