package com.warmhouse.temperature.service;

import com.warmhouse.temperature.model.HeatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HeatControlService {
    
    @Autowired
    private TemperatureService temperatureService;
    
    // Простое хранение состояния отопления в памяти
    private final Map<String, HeatState> heatStates = new ConcurrentHashMap<>();
    
    public HeatState getHeatState(String deviceId) {
        HeatState state = heatStates.computeIfAbsent(deviceId, id -> {
            HeatState newState = new HeatState();
            newState.setDeviceId(id);
            newState.setTargetTemperature(22.0);
            newState.setMode("AUTO");
            newState.setHeatingEnabled(false);
            newState.setStatus("ACTIVE");
            return newState;
        });
        
        // Получаем актуальную температуру от TemperatureService
        String location = mapDeviceIdToLocation(deviceId);
        double currentTemperature = temperatureService.getTemperature(location);
        state.setCurrentTemperature(currentTemperature);
        
        // Обновляем логику отопления на основе актуальной температуры
        updateHeatingLogic(state);
        
        System.out.println("=== GETTING HEAT STATE ===");
        System.out.println("Device ID: " + deviceId);
        System.out.println("Location: " + location);
        System.out.println("Current temperature: " + currentTemperature + "°C");
        System.out.println("Target temperature: " + state.getTargetTemperature() + "°C");
        System.out.println("Heating enabled: " + state.isHeatingEnabled());
        System.out.println("Current state: " + state);
        return state;
    }
    
    public void setMode(String deviceId, String mode) {
        HeatState state = getHeatState(deviceId);
        System.out.println("=== SETTING HEAT MODE ===");
        System.out.println("Device ID: " + deviceId);
        System.out.println("New mode: " + mode);
        System.out.println("Previous mode: " + state.getMode());
        
        state.setMode(mode);
        
        if ("OFF".equals(mode)) {
            state.setHeatingEnabled(false);
            state.setStatus("INACTIVE");
        } else {
            state.setStatus("ACTIVE");
        }
        
        System.out.println("Heat mode set to " + mode + " for device " + deviceId);
        System.out.println("Current state: " + state);
    }
    
    public void setTargetTemperature(String deviceId, double temperature) {
        HeatState state = getHeatState(deviceId);
        state.setTargetTemperature(temperature);
        
        // Обновляем логику отопления с актуальной температурой
        updateHeatingLogic(state);
        
        System.out.println("Target temperature set to " + temperature + "°C for device " + deviceId);
        System.out.println("Current temperature: " + state.getCurrentTemperature() + "°C");
        System.out.println("Heating enabled: " + state.isHeatingEnabled());
    }
    
    public void processCommand(String deviceId, String commandType, Object commandData) {
        System.out.println("Processing command " + commandType + " for device " + deviceId);
        
        if ("SET_MODE".equals(commandType)) {
            Map<String, Object> data = (Map<String, Object>) commandData;
            String mode = (String) data.get("mode");
            setMode(deviceId, mode);
        } else if ("SET_TEMPERATURE".equals(commandType)) {
            Map<String, Object> data = (Map<String, Object>) commandData;
            double temperature = ((Number) data.get("temperature")).doubleValue();
            setTargetTemperature(deviceId, temperature);
        }
    }
    
    /**
     * Maps device ID to location name for temperature service
     */
    private String mapDeviceIdToLocation(String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            return "default";
        }
        
        // Для temperature-module-factory-001 используем разные комнаты
        // Можно расширить логику для других deviceId
        if (deviceId.contains("temperature-module-factory-001")) {
            return "Living Room"; // По умолчанию для этого модуля
        }
        
        // Маппинг по deviceId (можно расширить)
        switch (deviceId) {
            case "temperature-module-factory-001":
                return "Living Room";
            case "temperature-module-factory-002":
                return "Bedroom";
            case "temperature-module-factory-003":
                return "Kitchen";
            default:
                return "default";
        }
    }
    
    /**
     * Updates heating logic based on current and target temperatures
     */
    private void updateHeatingLogic(HeatState state) {
        if ("OFF".equals(state.getMode())) {
            state.setHeatingEnabled(false);
            state.setStatus("INACTIVE");
        } else if ("AUTO".equals(state.getMode())) {
            // Автоматический режим: включаем отопление если текущая температура ниже целевой
            if (state.getCurrentTemperature() < state.getTargetTemperature()) {
                state.setHeatingEnabled(true);
                state.setStatus("ACTIVE");
            } else {
                state.setHeatingEnabled(false);
                state.setStatus("INACTIVE");
            }
        } else if ("MANUAL".equals(state.getMode())) {
            // Ручной режим: отопление включается вручную
            state.setStatus("ACTIVE");
        }
    }
}
