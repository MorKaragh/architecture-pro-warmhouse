package com.warmhouse.modules_gateway.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "module_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String moduleId;
    
    @Column(nullable = false)
    private String moduleType;
    
    @Column(nullable = false)
    private String homeId;
    
    @Column(nullable = false)
    private String baseUrl;
    
    @Column(nullable = false)
    private String status;
    
    @Column(nullable = false)
    private LocalDateTime registeredAt;
    
    @Column
    private LocalDateTime lastHeartbeat;
    
    @Column
    private String description;
}
