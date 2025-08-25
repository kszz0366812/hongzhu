package com.insightflow.service.report;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.dto.ReportDTO;
import com.insightflow.entity.report.Report;
import com.insightflow.vo.ReportVo;

public interface ReportService extends IService<Report> {

    /**
     * 分页查询报告列表
     */
    IPage<Report> getReportList(Integer page, Integer size, String type, String department);

    /**
     * 分页查询报告列表（包含用户名信息）
     */
    IPage<ReportVo> getReportVoList(Integer page, Integer size, String type, String department);

    /**
     * 创建报告
     */
    Long createReport(ReportDTO reportDTO);

    /**
     * 更新报告
     */
    boolean updateReport(ReportDTO reportDTO);

    /**
     * 删除报告
     */
    boolean deleteReport(Long id);

    /**
     * 获取报告统计信息
     */
    Object getReportStatistics();
} 