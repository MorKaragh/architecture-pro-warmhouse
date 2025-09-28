package com.warmhouse.modules_gateway.model.dto;

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
