package com.warmhouse.temperature.api;

import com.warmhouse.temperature.model.HeatCommand;
import com.warmhouse.temperature.model.HeatState;
import com.warmhouse.temperature.service.HeatControlService;
import com.warmhouse.temperature.service.TemperatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class TemperatureController {

    private final TemperatureService temperatureService;
    
    @Autowired
    private HeatControlService heatControlService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping("/temperature")
    public ResponseEntity<Map<String, Object>> getTemperature(
            @RequestParam(name = "location", required = false, defaultValue = "") String location,
            HttpServletRequest request) {
        
        log.info("=== TEMPERATURE API REQUEST ===");
        log.info("Location: {}", location);
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Query String: {}", request.getQueryString());
        log.info("Request Method: {}", request.getMethod());
        
        // Логируем все входящие заголовки
        log.info("Incoming Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("  {}: {}", headerName, headerValue);
        }
        
        // If no location is provided, use default
        if (location == null || location.isEmpty()) {
            location = "default";
        }
        
        // Map location to sensor ID for consistency
        String sensorId = mapLocationToSensorId(location);
        log.info("Mapped location {} to sensor ID: {}", location, sensorId);
        
        double valueCelsius = temperatureService.getTemperature(location);
        Map<String, Object> body = new HashMap<>();
        body.put("location", location);
        body.put("value", valueCelsius);
        body.put("unit", "C");
        body.put("sensor_id", sensorId);
        
        log.info("Temperature response: {}", body);
        log.info("=== END TEMPERATURE API REQUEST ===");
        
        return ResponseEntity.ok(body);
    }
    
    @GetMapping("/temperature/{sensorId}")
    public ResponseEntity<Map<String, Object>> getTemperatureBySensorId(
            @PathVariable String sensorId,
            HttpServletRequest request) {
        
        log.info("=== TEMPERATURE API REQUEST BY SENSOR ID ===");
        log.info("Sensor ID: {}", sensorId);
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Query String: {}", request.getQueryString());
        log.info("Request Method: {}", request.getMethod());
        
        // Логируем все входящие заголовки
        log.info("Incoming Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("  {}: {}", headerName, headerValue);
        }
        
        // Map sensor ID to location
        String location = mapSensorIdToLocation(sensorId);
        log.info("Mapped sensor ID {} to location: {}", sensorId, location);
        
        // Get temperature for the mapped location
        double valueCelsius = temperatureService.getTemperature(location);
        
        // Создаем ответ в формате, ожидаемом smart_home
        Map<String, Object> body = new HashMap<>();
        body.put("value", valueCelsius);
        body.put("unit", "°C");
        body.put("timestamp", java.time.Instant.now().toString());
        body.put("location", location);
        body.put("status", "active");
        body.put("sensor_id", sensorId);
        body.put("sensor_type", "temperature");
        body.put("description", "Temperature sensor reading for " + location);
        
        log.info("Temperature response for sensor {}: {}", sensorId, body);
        log.info("=== END TEMPERATURE API REQUEST BY SENSOR ID ===");
        
        return ResponseEntity.ok(body);
    }
    
    @PostMapping("/commands")
    public ResponseEntity<Map<String, Object>> processCommand(@RequestBody HeatCommand command) {
        System.out.println("=== TEMPERATURE API RECEIVED COMMAND ===");
        System.out.println("Command type: " + command.getType());
        System.out.println("Device ID: " + command.getDeviceId());
        System.out.println("Command data: " + command.getData());
        
        heatControlService.processCommand(command.getDeviceId(), command.getType(), command.getData());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Command processed successfully");
        response.put("deviceId", command.getDeviceId());
        response.put("commandType", command.getType());
        
        System.out.println("Command processed successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/heat/state/{deviceId}")
    public ResponseEntity<HeatState> getHeatState(@PathVariable String deviceId) {
        HeatState state = heatControlService.getHeatState(deviceId);
        return ResponseEntity.ok(state);
    }
    
    /**
     * Maps sensor ID to location name
     */
    private String mapSensorIdToLocation(String sensorId) {
        if (sensorId == null || sensorId.isEmpty()) {
            return "Unknown";
        }
        
        switch (sensorId) {
            case "1":
                return "Living Room";
            case "2":
                return "Bedroom";
            case "3":
                return "Kitchen";
            default:
                return "Unknown";
        }
    }
    
    /**
     * Maps location name to sensor ID
     */
    private String mapLocationToSensorId(String location) {
        if (location == null || location.isEmpty()) {
            return "0";
        }
        
        switch (location) {
            case "Living Room":
                return "1";
            case "Bedroom":
                return "2";
            case "Kitchen":
                return "3";
            default:
                return "0";
        }
    }
}


