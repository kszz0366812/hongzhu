package com.insightflow.entity.template;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("itf_template")
public class ItfTemplate extends BaseEntity {
    
    private String tepName;
    
    private String resourceSql;
    
    private Integer repType;
    
    private Integer isPage;
} 