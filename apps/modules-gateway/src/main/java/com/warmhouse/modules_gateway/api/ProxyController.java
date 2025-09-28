package com.warmhouse.modules_gateway.api;

import com.warmhouse.modules_gateway.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Slf4j
public class ProxyController {
    
    private final ProxyService proxyService;
    
    @GetMapping("/{moduleId}/proxy/**")
    public ResponseEntity<String> proxyGet(
            @PathVariable String moduleId,
            HttpServletRequest request) {
        
        log.info("=== INCOMING REQUEST ===");
        log.info("Module ID: {}", moduleId);
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Query String: {}", request.getQueryString());
        log.info("Request Method: {}", request.getMethod());
        
        // Логируем все входящие заголовки
        log.info("Incoming Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("  {}: {}", headerName, headerValue);
        }
        
        String targetPath = extractTargetPath(request.getRequestURI(), moduleId);
        String queryString = request.getQueryString();
        
        // Добавляем query параметры к пути, если они есть
        if (queryString != null && !queryString.isEmpty()) {
            targetPath += "?" + queryString;
        }
        
        log.info("Extracted target path: {}", targetPath);
        log.info("=== END INCOMING REQUEST ===");
        
        ResponseEntity<String> response = proxyService.proxyRequest(moduleId, HttpMethod.GET, targetPath, null, null);
        
        // Дополнительная очистка заголовков на уровне контроллера
        HttpHeaders cleanHeaders = new HttpHeaders();
        response.getHeaders().forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Transfer-Encoding") && 
                !key.equalsIgnoreCase("Connection") && 
                !key.equalsIgnoreCase("Keep-Alive")) {
                cleanHeaders.put(key, values);
            }
        });
        
        // Устанавливаем Content-Length
        if (response.getBody() != null) {
            cleanHeaders.set("Content-Length", String.valueOf(response.getBody().length()));
        }
        
        log.info("Final response headers:");
        cleanHeaders.forEach((key, values) -> {
            log.info("  {}: {}", key, values);
        });
        
        return new ResponseEntity<>(response.getBody(), cleanHeaders, response.getStatusCode());
    }
    
    @PostMapping("/{moduleId}/proxy/**")
    public ResponseEntity<String> proxyPost(
            @PathVariable String moduleId,
            @RequestBody(required = false) String body,
            HttpServletRequest request) {
        
        log.info("Proxying POST request to module {}: {}", moduleId, request.getRequestURI());
        
        String targetPath = extractTargetPath(request.getRequestURI(), moduleId);
        String queryString = request.getQueryString();
        
        // Добавляем query параметры к пути, если они есть
        if (queryString != null && !queryString.isEmpty()) {
            targetPath += "?" + queryString;
        }
        
        return proxyService.proxyRequest(moduleId, HttpMethod.POST, targetPath, null, body);
    }
    
    @PutMapping("/{moduleId}/proxy/**")
    public ResponseEntity<String> proxyPut(
            @PathVariable String moduleId,
            @RequestBody(required = false) String body,
            HttpServletRequest request) {
        
        log.info("Proxying PUT request to module {}: {}", moduleId, request.getRequestURI());
        
        String targetPath = extractTargetPath(request.getRequestURI(), moduleId);
        String queryString = request.getQueryString();
        
        // Добавляем query параметры к пути, если они есть
        if (queryString != null && !queryString.isEmpty()) {
            targetPath += "?" + queryString;
        }
        
        return proxyService.proxyRequest(moduleId, HttpMethod.PUT, targetPath, null, body);
    }
    
    @DeleteMapping("/{moduleId}/proxy/**")
    public ResponseEntity<String> proxyDelete(
            @PathVariable String moduleId,
            HttpServletRequest request) {
        
        log.info("Proxying DELETE request to module {}: {}", moduleId, request.getRequestURI());
        
        String targetPath = extractTargetPath(request.getRequestURI(), moduleId);
        String queryString = request.getQueryString();
        
        // Добавляем query параметры к пути, если они есть
        if (queryString != null && !queryString.isEmpty()) {
            targetPath += "?" + queryString;
        }
        
        return proxyService.proxyRequest(moduleId, HttpMethod.DELETE, targetPath, null, null);
    }
    
    @PatchMapping("/{moduleId}/proxy/**")
    public ResponseEntity<String> proxyPatch(
            @PathVariable String moduleId,
            @RequestBody(required = false) String body,
            HttpServletRequest request) {
        
        log.info("Proxying PATCH request to module {}: {}", moduleId, request.getRequestURI());
        
        String targetPath = extractTargetPath(request.getRequestURI(), moduleId);
        String queryString = request.getQueryString();
        
        // Добавляем query параметры к пути, если они есть
        if (queryString != null && !queryString.isEmpty()) {
            targetPath += "?" + queryString;
        }
        
        return proxyService.proxyRequest(moduleId, HttpMethod.PATCH, targetPath, null, body);
    }
    
    private String extractTargetPath(String requestUri, String moduleId) {
        // Убираем префикс "/api/v1/modules/{moduleId}/proxy" и оставляем только путь к модулю
        String proxyPrefix = "/api/v1/modules/" + moduleId + "/proxy";
        String targetPath = requestUri.substring(proxyPrefix.length());
        
        // Если путь пустой, возвращаем "/"
        if (targetPath.isEmpty()) {
            targetPath = "/";
        }
        
        return targetPath;
    }
    
}
