package com.warmhouseyandex.heat_service.service;

import com.warmhouseyandex.heat_service.integration.ModuleAdapter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandService {
    
    @Autowired
    private ModuleAdapter moduleAdapter;
    
    public void setMode(String deviceId, String mode) {
        // Отправляем команду к устройству отопления через gateway
        System.out.println("Setting heat mode to " + mode + " for device " + deviceId);
        moduleAdapter.sendCommand(deviceId, "SET_MODE", Map.of("mode", mode));
    }
    
    public void setTargetTemperature(String deviceId, double celsius) {
        // Отправляем команду к устройству отопления через gateway
        System.out.println("Setting target temperature to " + celsius + "°C for device " + deviceId);
        moduleAdapter.sendCommand(deviceId, "SET_TEMPERATURE", Map.of("temperature", celsius));
    }
}
