package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReportDTO {

    /**
     * 报告ID
     */
    private Long id;

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

} 