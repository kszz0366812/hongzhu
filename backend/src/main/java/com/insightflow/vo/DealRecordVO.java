package com.insightflow.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DealRecordVO {
    private Long id;
    private Long visitId;
    private Long dealEmployeeId;
    private String dealEmployeeName;
    private String dealEmployeeCode;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private Long productId;
    private String productName;
    private String productCode;
    private String specification;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime dealTime;
    private String customerManager;
    private String dealRemark;
} 