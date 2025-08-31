package com.insightflow.common.config;

import com.insightflow.common.util.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: sy
 * @CreateTime: 2025-05-31
 * @Description: 认证过滤器
 * @Version: 1.0
 */
@Order(10)
@Component
public class JwtAuthFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String requestURI = httpRequest.getRequestURI();
        
        // 添加详细日志
        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Authorization Header: " + httpRequest.getHeader("Authorization"));
        
        // 定义需要认证的路径模式
        String[] authRequiredPatterns = {"create", "update", "delete", "save"};

        boolean authRequired = false;
        for (String pattern : authRequiredPatterns) {
            if (requestURI.contains(pattern)) {
                authRequired = true;
                System.out.println("Pattern matched: " + pattern);
                break;
            }
        }

        if (authRequired) {
            String token = getToken(httpRequest);
            System.out.println("Extracted token: " + token);
            System.out.println("Token validation result: " + JwtUtils.validateToken(token));
            
            if (!JwtUtils.validateToken(token)) {
                System.out.println("Token validation failed!");
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
                return;
            }
            System.out.println("Token validation successful!");
        }

        // 继续执行过滤器链
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getToken(HttpServletRequest request) {
         String token = request.getHeader("Authorization");
         if (token != null && token.startsWith("Bearer ")) {
             return token.substring(7);
         }
          return null;
    }


    @Override
    public void destroy() {
        super.destroy();
    }
}
