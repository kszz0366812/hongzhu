package com.insightflow.dto;

import lombok.Data;
import java.util.List;

@Data
public class TerminalQueryDTO {
    private String terminalCode;
    private String terminalName;
    private String terminalType;
    private List<String> tags;
    private String customerManager;
    private Integer isScheduled;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private Integer status;
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式：ASC, DESC
} 