package com.insightflow.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class ExcelTemplateConfig {
    private final Map<String, TemplateDefinition> templateMap = new HashMap<>();
    
    @Value("${excel.template.path:templates/*_template.yml}")
    private String templatePathPattern;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(templatePathPattern);
        
        for (Resource resource : resources) {
            TemplateDefinition template = mapper.readValue(resource.getInputStream(), TemplateDefinition.class);
            templateMap.put(template.getCode(), template);
        }
    }

    public TemplateDefinition getTemplate(String code) {
        return templateMap.get(code);
    }

    @Data
    public static class TemplateDefinition {
        private String code;
        private String name;
        private String description;
        private List<SheetDefinition> sheets;
    }

    @Data
    public static class SheetDefinition {
        private String name;
        private String title;
        private String dataSource;
        private List<ColumnDefinition> columns;
    }

    @Data
    public static class ColumnDefinition {
        private String field;
        private String title;
        private String type;
        private Map<String, String> options;
    }
} 