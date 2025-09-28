package com.warmhouse.temperature.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRegistrationRequest {
    private String moduleId;
    private String moduleType;
    private String homeId;
    private String baseUrl;
    private String description;
}
