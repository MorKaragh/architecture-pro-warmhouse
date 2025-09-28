package com.warmhouseyandex.heat_service.service;

import com.warmhouseyandex.heat_service.integration.ModuleAdapter;
import com.warmhouseyandex.heat_service.model.HeatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoadDataService {
    
    @Autowired
    private ModuleAdapter moduleAdapter;
    
    public HeatState getCurrentState(String deviceId) {
        // Получаем реальное состояние от устройства отопления через gateway
        System.out.println("Getting current state for device " + deviceId);
        return moduleAdapter.readState(deviceId);
    }
}
