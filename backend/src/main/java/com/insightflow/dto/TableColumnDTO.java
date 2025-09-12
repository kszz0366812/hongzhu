package com.insightflow.dto;

import lombok.Data;

/**
 * @Author: sy
 * @CreateTime: 2025-06-10
 * @Description: 表字段
 * @Version: 1.0
 */
@Data
public class TableColumnDTO {
    private String columnName;//字段名称
    private String columnType;//字段类型
    private String columnComment;//字段注释
    private Boolean isNullable;//是否为空
    private String defaultValue;//默认值
}