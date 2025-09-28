package com.warmhouseyandex.heat_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeatState {
    private String deviceId; // ID устройства (модуля) в gateway
    private double currentTemperature;
    private double targetTemperature;
    private String mode; // AUTO, MANUAL, OFF
    private boolean heatingEnabled;
    private String status; // ACTIVE, INACTIVE, ERROR
}
