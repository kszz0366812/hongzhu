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
     * 拜访人ID
     */
    @TableField("visitor_id")
    private Long visitorId;

    /**
     * 拜访人姓名
     */
    private String visitorName;

    /**
     * 拜访终端ID
     */
    @TableField("terminal_id")
    private Long terminalId;

    /**
     * 拜访终端名称
     */
    private String terminalName;

    /**
     * 拜访时间
     */
    @TableField("visit_time")
    private LocalDateTime visitTime;

    /**
     * 拜访目的
     */
    private String purpose;

    /**
     * 拜访内容
     */
    private String content;

    /**
     * 拜访结果
     */
    private String result;

    /**
     * 下次拜访时间
     */
    @TableField("next_visit_time")
    private LocalDateTime nextVisitTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否处理
     */
    @TableField("is_deal")
    private Integer isDeal;

    /**
     * 拜访类型
     */
    @TableField("visit_type")
    private String visitType;

    /**
     * 拜访状态
     */
    @TableField("visit_status")
    private String visitStatus;

    /**
     * 拜访备注
     */
    @TableField("visit_notes")
    private String visitNotes;

    /**
     * 拜访地点
     */
    @TableField("visit_location")
    private String visitLocation;

    /**
     * 拜访时长
     */
    @TableField("visit_duration")
    private Integer visitDuration;
} 