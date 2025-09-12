package com.insightflow.dto;

import lombok.Data;

@Data
public class DataDictionaryQueryDTO {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String name;
    
    private String code;
    
    private String type;
    
    private Long parentId;
    
    private Integer status;
} 