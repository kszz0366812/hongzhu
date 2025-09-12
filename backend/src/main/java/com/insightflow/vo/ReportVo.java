package com.insightflow.vo;

import com.insightflow.entity.attachment.Attachment;
import com.insightflow.entity.report.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-08-05
 * @Description: 报告扩展类
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportVo extends Report {

    private String authorName; // 用户名

    private List<Attachment> attachments;
}
