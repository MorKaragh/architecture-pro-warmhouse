package com.warmhouse.temperature.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeatCommand {
    private String type; // SET_MODE, SET_TEMPERATURE
    private String deviceId;
    private Object data;
}
