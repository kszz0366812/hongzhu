package com.insightflow.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class DataDictionaryDTO {
    
    private Long id;
    
    @NotBlank(message = "字典编码不能为空")
    private String code;
    
    @NotBlank(message = "字典名称不能为空")
    private String name;
    
    private Long parentId;
    
    @NotBlank(message = "字典类型不能为空")
    private String type;
    
    private Integer sort; // 可为null
    
    private Integer status;
} 