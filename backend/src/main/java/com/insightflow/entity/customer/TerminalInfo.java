package com.insightflow.entity.customer;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("terminal_info")
public class TerminalInfo extends BaseEntity {
    @TableField("terminal_code")
    private String terminalCode;
    @TableField("terminal_name")
    private String terminalName;
    @TableField("terminal_type")
    private String terminalType;
    // 配置字段策略，允许为空
    @TableField(value = "tags")
    private String tags;
    @TableField("customer_manager")
    private String customerManager;
    @TableField("is_scheduled")
    private Integer isScheduled;
} 