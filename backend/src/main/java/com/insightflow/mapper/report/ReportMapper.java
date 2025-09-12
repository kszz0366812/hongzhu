package com.insightflow.mapper.report;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.report.Report;
import com.insightflow.vo.ReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    
    /**
     * 分页查询报告列表（包含用户名信息）
     */
    List<ReportVo> selectReportVoPage(@Param("type") String type, @Param("department") String department);
} 