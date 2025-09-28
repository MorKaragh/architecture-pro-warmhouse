package com.warmhouse.modules_gateway.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Фильтр для обработки chunked encoding в прокси-запросах
 */
@Component
@Order(1)
@Slf4j
public class ChunkedEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Логируем входящие заголовки для отладки
        if (log.isDebugEnabled()) {
            log.debug("Request headers: {}", httpRequest.getHeaderNames());
            httpRequest.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                log.debug("Header {}: {}", headerName, httpRequest.getHeader(headerName));
            });
        }
        
        // Убираем проблемные заголовки из ответа
        httpResponse.setHeader("Connection", "close");
        httpResponse.setHeader("Transfer-Encoding", "");
        
        // Продолжаем обработку запроса
        chain.doFilter(request, response);
    }
}
