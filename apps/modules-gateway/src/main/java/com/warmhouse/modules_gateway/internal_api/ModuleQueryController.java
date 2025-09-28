package com.warmhouse.modules_gateway.internal_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal")
public class ModuleQueryController {
    
    @GetMapping("/test")
    public ResponseEntity<String> getModuleInfo() {
        return ResponseEntity.ok("Hello from gateway");
    }

}
