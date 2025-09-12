package com.insightflow.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-06-10
 * @Description: 表结构
 * @Version: 1.0
 */
@Data
public  class DbTableDTO {
    private String tableName;
    private String tableComment;
    private List<TableColumnDTO> columns;
}