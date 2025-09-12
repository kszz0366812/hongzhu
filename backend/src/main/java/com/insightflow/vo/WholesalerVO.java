package com.insightflow.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WholesalerVO {
    private Long id;
    private String dealerCode;
    private String dealerName;
    private String level;
    private String contactPerson;
    private String contactPhone;
    private String customerManager;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private Integer status;  // 状态：0-禁用，1-启用
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 