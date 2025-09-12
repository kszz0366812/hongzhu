package com.insightflow.entity.template;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pv_template")
public class PvTemplate extends BaseEntity {
    private String tepName;
    private Long itfId;
    private Integer isPage;
} 