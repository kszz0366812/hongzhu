package com.insightflow.dto;

import lombok.Data;


@Data
public class ItfTemplateDTO {
    
    private Integer id;
    private String tepName;
    private String resourceSql;
    private Integer repType; 
    private Integer isPage;
} 