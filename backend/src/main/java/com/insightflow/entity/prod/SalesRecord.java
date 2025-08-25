package com.insightflow.entity.prod;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_record")
public class SalesRecord extends BaseEntity {
    private String salesOrderNo;
    private LocalDateTime salesDate;
    private String customerName;
    private String customerLevel;
    private String distributor;
    private String customerManager;
    private String salesperson;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer isGift;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    // 区域相关字段
    private String regionLevel1; // 大区
    private String regionLevel2; // 地市
    private String regionLevel3; // 区域
} 