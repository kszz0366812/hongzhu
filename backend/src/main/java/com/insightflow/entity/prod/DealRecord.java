package com.insightflow.entity.prod;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("deal_record")
public class DealRecord extends BaseEntity {
    
    @TableField("deal_time")
    private LocalDateTime dealTime;
    
    @TableField("visit_id")
    private Long visitId;
    
    @TableField("deal_employee_id")
    private Long dealEmployeeId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("unit")
    private String unit;
    
    @TableField("unit_price")
    private BigDecimal unitPrice;
    
    @TableField("total_price")
    private BigDecimal totalPrice;
} 