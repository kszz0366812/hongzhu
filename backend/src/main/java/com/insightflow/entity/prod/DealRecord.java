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
    
    @TableField("sales_order_no")
    private String salesOrderNo;
    
    @TableField("sales_date_time")
    private LocalDateTime salesDateTime;
    
    @TableField("customer_name")
    private String customerName;
    
    @TableField("distributor")
    private String distributor;
    
    @TableField("distributor_type")
    private String distributorType;
    
    @TableField("customer_manager")
    private String customerManager;
    
    @TableField("salesperson")
    private String salesperson;
    
    @TableField("product_name")
    private String productName;
    
    @TableField("specification")
    private String specification;
    
    @TableField("sales_quantity")
    private BigDecimal salesQuantity;
    
    @TableField("customer_code")
    private String customerCode;
    
    @TableField("customer_type")
    private String customerType;
    
    @TableField("is_gift")
    private Integer isGift;
    
    @TableField("sales_unit")
    private String salesUnit;
    
    @TableField("salesperson_supervisor")
    private String salespersonSupervisor;
    
    @TableField("customer_category")
    private String customerCategory;
    
    @TableField("conversion_unit")
    private String conversionUnit;
    
    @TableField("product_series")
    private String productSeries;
} 