package com.insightflow.mapper.report;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.entity.report.Report;
import com.insightflow.vo.ReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    
    /**
     * 分页查询报告列表（包含用户名信息）
     */
    IPage<ReportVo> selectReportVoPage(Page<ReportVo> page, @Param("type") String type, @Param("department") String department);
} 