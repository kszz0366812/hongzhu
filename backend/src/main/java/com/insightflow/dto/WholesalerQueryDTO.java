package com.insightflow.dto;

import lombok.Data;

@Data
public class WholesalerQueryDTO {
    private String dealerCode;
    private String dealerName;
    private String level;
    private String contactPerson;
    private String customerManager;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private Integer status;
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式：ASC, DESC
} 