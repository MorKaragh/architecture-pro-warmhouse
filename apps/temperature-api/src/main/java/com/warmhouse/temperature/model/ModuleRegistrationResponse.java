package com.warmhouse.temperature.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRegistrationResponse {
    private boolean success;
    private String message;
    private String moduleId;
}
