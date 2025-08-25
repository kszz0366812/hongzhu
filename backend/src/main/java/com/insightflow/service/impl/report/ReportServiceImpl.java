package com.insightflow.service.impl.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.dto.ReportDTO;
import com.insightflow.entity.report.Report;
import com.insightflow.mapper.report.ReportMapper;
import com.insightflow.service.report.ReportService;
import com.insightflow.vo.ReportVo;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Override
    public IPage<Report> getReportList(Integer page, Integer size, String type, String department) {
        Page<Report> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(Report::getType, type);
        }
        
        if (StringUtils.hasText(department)) {
            wrapper.eq(Report::getDepartment, department);
        }
        
        wrapper.orderByDesc(Report::getCreateTime);

        return this.page(pageParam, wrapper);
    }

    @Override
    public IPage<ReportVo> getReportVoList(Integer page, Integer size, String type, String department) {
        Page<ReportVo> pageParam = new Page<>(page, size);
        return baseMapper.selectReportVoPage(pageParam, type, department);
    }

    @Override
    public Long createReport(ReportDTO reportDTO) {
        Report report = new Report();
        BeanUtils.copyProperties(reportDTO, report);
        
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(report);
        
        report.setLikes(0);
        report.setComments(0);
        
        this.save(report);
        return report.getId();
    }

    @Override
    public boolean updateReport(ReportDTO reportDTO) {
        Report report = new Report();
        BeanUtils.copyProperties(reportDTO, report);
        
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(report);
        
        return this.updateById(report);
    }

    @Override
    public boolean deleteReport(Long id) {
        return this.removeById(id);
    }

    @Override
    public Object getReportStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总报告数
        long totalReports = this.count();
        statistics.put("totalReports", totalReports);
        
        // 各类型报告数
        long dailyReports = this.count(new LambdaQueryWrapper<Report>().eq(Report::getType, "DAILY"));
        long weeklyReports = this.count(new LambdaQueryWrapper<Report>().eq(Report::getType, "WEEKLY"));
        long monthlyReports = this.count(new LambdaQueryWrapper<Report>().eq(Report::getType, "MONTHLY"));
        long otherReports = this.count(new LambdaQueryWrapper<Report>().eq(Report::getType, "OTHER"));
        
        statistics.put("dailyReports", dailyReports);
        statistics.put("weeklyReports", weeklyReports);
        statistics.put("monthlyReports", monthlyReports);
        statistics.put("otherReports", otherReports);
        
        return statistics;
    }
} 