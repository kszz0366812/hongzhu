package com.insightflow.entity.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("visit_record")
public class VisitRecord extends BaseEntity {

    /**
     * 拜访员工编码
     */
    @TableField("visitor_code")
    private String visitorCode;

    /**
     * 拜访时间
     */
    @TableField("visit_time")
    private LocalDateTime visitTime;

    /**
     * 拜访终端编码
     */
    @TableField("terminal_code")
    private String terminalCode;

    /**
     * 是否成交（0-否，1-是）
     */
    @TableField("is_deal")
    private Integer isDeal;
    
    // ========== 查询结果额外字段（不映射到数据库） ==========
    
    /**
     * 终端名称（从terminal_info表关联查询）
     */
    @TableField(exist = false)
    private String terminalName;
    
    /**
     * 拜访人姓名（从terminal_info表的customer_manager字段）
     */
    @TableField(exist = false)
    private String visitorName;
    
    /**
     * 成交状态描述（是/否）
     */
    @TableField(exist = false)
    private String dealStatus;
} 