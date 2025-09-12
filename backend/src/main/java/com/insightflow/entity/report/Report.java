package com.insightflow.entity.report;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_report")
public class Report extends BaseEntity {

    /**
     * 报告类型(DAILY,WEEKLY,MONTHLY,OTHER)
     */
    private String type;

    /**
     * 报告标题
     */
    private String title;

    /**
     * 部门
     */
    private String department;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 工作内容
     */
    private String content;

    /**
     * 遇到的问题
     */
    private String issues;

    /**
     * 下期计划
     */
    private String plan;

    /**
     * 报告日期
     */
    private LocalDate reportDate;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 评论数
     */
    private Integer comments;
} 