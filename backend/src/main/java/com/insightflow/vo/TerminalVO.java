package com.insightflow.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TerminalVO {
    private Long id;
    private String terminalCode;
    private String terminalName;
    private String terminalType;
    private List<String> tags;
    private String customerManager;
    private Integer isScheduled;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private Integer status;  // 状态：0-禁用，1-启用
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 