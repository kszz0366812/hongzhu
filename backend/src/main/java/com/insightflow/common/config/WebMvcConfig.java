package com.insightflow.common.config;

import com.insightflow.common.interceptor.ExportPermissionInterceptor;
import com.insightflow.common.interceptor.ImportPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ExportPermissionInterceptor exportPermissionInterceptor;

    @Autowired
    private ImportPermissionInterceptor importPermissionInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(exportPermissionInterceptor)
                .addPathPatterns("/api/excel/export/**");
        registry.addInterceptor(importPermissionInterceptor)
                .addPathPatterns("/api/excel/import/**");
    }
} 