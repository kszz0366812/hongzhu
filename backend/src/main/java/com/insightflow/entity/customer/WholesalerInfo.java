package com.insightflow.entity.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wholesaler_info")
public class WholesalerInfo extends BaseEntity {
    
    @TableField("dealer_code")
    private String dealerCode;
    
    @TableField("dealer_name")
    private String dealerName;
    
    @TableField("level")
    private String level;
    
    @TableField("contact_person")
    private String contactPerson;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    @TableField("customer_manager")
    private String customerManager;
} 