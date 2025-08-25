package com.insightflow.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_dictionary")
public class DataDictionary extends BaseEntity {
    @TableField("code")
    private String code;
    @TableField("name")
    private String name;
    @TableField("parent_id")
    private Long parentId;
    @TableField("type")
    private String type;
    @TableField("sort")
    private Integer sort; // 可为null
    @TableField("status")
    private Integer status;
} 