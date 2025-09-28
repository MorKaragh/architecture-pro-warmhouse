package com.warmhouseyandex.heat_service.integration;

import com.warmhouseyandex.heat_service.model.HeatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ModuleAdapter {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${gateway.url:http://modules-gateway:8083}")
    private String gatewayUrl;
    
    /**
     * Отправляет команду к устройству отопления через gateway
     */
    public void sendCommand(String deviceId, String commandType, Object commandData) {
        try {
            // Формируем URL для отправки команды к устройству через gateway
            String url = String.format("%s/api/v1/modules/%s/proxy/commands", gatewayUrl, deviceId);
            
            Map<String, Object> command = new HashMap<>();
            command.put("type", commandType);
            command.put("deviceId", deviceId);
            command.put("data", commandData);
            
            System.out.println("Sending command to device: " + commandType + " for device " + deviceId);
            System.out.println("Command URL: " + url);
            System.out.println("Command data: " + command);
            
            // Отправляем команду через gateway
            Map<String, Object> response = restTemplate.postForObject(url, command, Map.class);
            System.out.println("Command response: " + response);
            
        } catch (Exception e) {
            System.err.println("Error sending command to device: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Читает состояние от устройства отопления через gateway
     */
    public HeatState readState(String deviceId) {
        try {
            // Получаем состояние отопления от устройства через gateway
            String url = String.format("%s/api/v1/modules/%s/proxy/heat/state/%s", gatewayUrl, deviceId, deviceId);
            
            System.out.println("Reading heat state from device " + deviceId);
            
            // Получаем состояние отопления
            HeatState state = restTemplate.getForObject(url, HeatState.class);
            
            if (state != null) {
                return state;
            }
            
        } catch (Exception e) {
            System.err.println("Error reading heat state from device: " + e.getMessage());
        }
        
        // Возвращаем состояние по умолчанию в случае ошибки
        HeatState defaultState = new HeatState();
        defaultState.setDeviceId(deviceId);
        defaultState.setCurrentTemperature(20.0);
        defaultState.setTargetTemperature(22.0);
        defaultState.setMode("AUTO");
        defaultState.setHeatingEnabled(false);
        defaultState.setStatus("ERROR");
        return defaultState;
    }
}
