package com.warmhouse.temperature.service;

import com.warmhouse.temperature.model.ModuleRegistrationRequest;
import com.warmhouse.temperature.model.ModuleRegistrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayRegistrationService {
    
    private final RestTemplate restTemplate;
    
    @Value("${gateway.url:http://modules-gateway:8083}")
    private String gatewayUrl;
    
    @Value("${module.home.id:default-home}")
    private String homeId;
    
    @Value("${module.id:temperature-module-factory-001}")
    private String moduleId;
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    private String baseUrl;
    
    @PostConstruct
    public void init() {
        this.baseUrl = "http://temperature-api:" + serverPort;
        
        log.info("Initializing temperature module with factory ID: {} and URL: {}", moduleId, baseUrl);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application is ready, starting gateway registration...");
        registerWithGateway();
    }
    
    @Async
    public void registerWithGateway() {
        int maxRetries = 10;
        int retryDelaySeconds = 5;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("Attempting to register with gateway (attempt {}/{})", attempt, maxRetries);
                
                ModuleRegistrationRequest request = new ModuleRegistrationRequest();
                request.setModuleId(moduleId);
                request.setModuleType("TEMPERATURE");
                request.setHomeId(homeId);
                request.setBaseUrl(baseUrl);
                request.setDescription("Temperature sensor module for smart home");
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                HttpEntity<ModuleRegistrationRequest> entity = new HttpEntity<>(request, headers);
                
                String registrationUrl = gatewayUrl + "/api/v1/modules/register";
                ResponseEntity<ModuleRegistrationResponse> response = restTemplate.exchange(
                    registrationUrl,
                    HttpMethod.POST,
                    entity,
                    ModuleRegistrationResponse.class
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().isSuccess()) {
                    log.info("Successfully registered with gateway: {}", response.getBody().getMessage());
                    
                    // Start heartbeat process
                    startHeartbeat();
                    return;
                } else {
                    log.warn("Registration failed: {}", response.getBody() != null ? response.getBody().getMessage() : "Unknown error");
                }
                
            } catch (Exception e) {
                log.error("Registration attempt {} failed: {}", attempt, e.getMessage());
            }
            
            if (attempt < maxRetries) {
                log.info("Retrying registration in {} seconds...", retryDelaySeconds);
                try {
                    Thread.sleep(retryDelaySeconds * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Registration thread interrupted");
                    return;
                }
            }
        }
        
        log.error("Failed to register with gateway after {} attempts", maxRetries);
    }
    
    @Async
    public void startHeartbeat() {
        log.info("Starting heartbeat process for module {}", moduleId);
        
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String heartbeatUrl = gatewayUrl + "/api/v1/modules/" + moduleId + "/heartbeat?homeId=" + homeId;
                
                ResponseEntity<ModuleRegistrationResponse> response = restTemplate.exchange(
                    heartbeatUrl,
                    HttpMethod.POST,
                    null,
                    ModuleRegistrationResponse.class
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().isSuccess()) {
                    log.debug("Heartbeat sent successfully");
                } else {
                    log.warn("Heartbeat failed: {}", response.getBody() != null ? response.getBody().getMessage() : "Unknown error");
                }
                
            } catch (Exception e) {
                log.error("Heartbeat failed: {}", e.getMessage());
            }
            
            try {
                Thread.sleep(30000); // Send heartbeat every 30 seconds
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.info("Heartbeat thread interrupted");
                break;
            }
        }
    }
    
    public String getModuleId() {
        return moduleId;
    }
    
    public String getHomeId() {
        return homeId;
    }
}
