package com.warmhouseyandex.heat_service.api;

import com.warmhouseyandex.heat_service.model.HeatState;
import com.warmhouseyandex.heat_service.service.LoadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/heat")
public class LoadDataController {

    @Autowired
    private LoadDataService loadDataService;
    
    @GetMapping("/state/{deviceId}")
    public ResponseEntity<HeatState> getCurrentState(@PathVariable String deviceId) {
        HeatState state = loadDataService.getCurrentState(deviceId);
        return ResponseEntity.ok(state);
    }
    
    @GetMapping("/test")
    public String test() {
        return "Heat Service is running!";
    }
    
}
