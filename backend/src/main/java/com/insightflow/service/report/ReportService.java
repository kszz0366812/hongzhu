package com.insightflow.service.report;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.dto.ReportDTO;
import com.insightflow.entity.report.Report;
import com.insightflow.vo.ReportVo;

import java.util.List;

public interface ReportService extends IService<Report> {

    /**
     * 分页查询报告列表
     */
    PageInfo<Report> getReportList(Integer page, Integer size, String type, String department);

    /**
     * 分页查询报告VO列表
     */
    PageInfo<ReportVo> getReportVoList(Integer page, Integer size, String type, String department);

    /**
     * 获取报告列表（不分页）
     */
    List<Report> getReportList(String type, String department);

    /**
     * 获取报告VO列表（不分页）
     */
    List<ReportVo> getReportVoList(String type, String department);

    /**
     * 创建报告
     */
    Long createReport(ReportDTO reportDTO);

    /**
     * 更新报告
     */
    boolean updateReport(ReportDTO reportDTO);



    /**
     * 获取报告统计信息
     */
    Object getReportStatistics();
}