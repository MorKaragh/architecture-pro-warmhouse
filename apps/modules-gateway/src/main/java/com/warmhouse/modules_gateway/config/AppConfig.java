package com.warmhouse.modules_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        // Используем SimpleClientHttpRequestFactory с настройками для предотвращения chunked encoding
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Добавляем интерцептор для управления заголовками
        restTemplate.getInterceptors().add((request, body, execution) -> {
            // Убираем все Transfer-Encoding заголовки
            request.getHeaders().remove("Transfer-Encoding");
            // Убираем Connection заголовки, которые могут влиять на кодирование
            request.getHeaders().remove("Connection");
            // Убираем Keep-Alive заголовки
            request.getHeaders().remove("Keep-Alive");
            // Устанавливаем Content-Length если есть тело запроса
            if (body != null && body.length > 0) {
                request.getHeaders().set("Content-Length", String.valueOf(body.length));
            }
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
}
