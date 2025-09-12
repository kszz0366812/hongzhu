package com.insightflow.entity.prod;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_info")
public class ProductInfo extends BaseEntity {
    
    @TableField("product_code")
    private String productCode;
    
    @TableField("product_name")
    private String productName;
    
    @TableField("specification")
    private String specification;
    
    @TableField("unit_price")
    private BigDecimal unitPrice;
    
    @TableField("case_price")
    private BigDecimal casePrice;
    
    @TableField("series")
    private String series;
} 