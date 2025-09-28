package com.warmhouseyandex.heat_service.api;

import com.warmhouseyandex.heat_service.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/heat/commands")
public class CommandsController {
    
    @Autowired
    private CommandService commandService;
    
    @PostMapping("/mode")
    public ResponseEntity<Void> setMode(@RequestParam String deviceId, @RequestParam String mode) {
        commandService.setMode(deviceId, mode);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/temperature")
    public ResponseEntity<Void> setTargetTemperature(@RequestParam String deviceId, @RequestParam double temperature) {
        commandService.setTargetTemperature(deviceId, temperature);
        return ResponseEntity.ok().build();
    }
}
