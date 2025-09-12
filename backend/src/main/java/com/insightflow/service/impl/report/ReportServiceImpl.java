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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Report report) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(report);
        return super.save(report);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Report> reportList) {
        // 批量设置创建信息
        reportList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(reportList);
    }

    @Override
    public PageInfo<Report> getReportList(Integer page, Integer size, String type, String department) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        
        // 类型筛选
        if (StringUtils.hasText(type)) {
            wrapper.eq(Report::getType, type);
        }
        
        // 部门筛选
        if (StringUtils.hasText(department)) {
            wrapper.eq(Report::getDepartment, department);
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByDesc(Report::getCreateTime);
        
        // 执行查询并返回PageInfo
        List<Report> reportList = list(wrapper);
        return new PageInfo<>(reportList);
    }

    @Override
    public PageInfo<ReportVo> getReportVoList(Integer page, Integer size, String type, String department) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        
        // 调用Mapper的分页查询方法
        List<ReportVo> reportVoList = baseMapper.selectReportVoPage(type, department);
        return new PageInfo<>(reportVoList);
    }

    @Override
    public List<Report> getReportList(String type, String department) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        
        // 类型筛选
        if (StringUtils.hasText(type)) {
            wrapper.eq(Report::getType, type);
        }
        
        // 部门筛选
        if (StringUtils.hasText(department)) {
            wrapper.eq(Report::getDepartment, department);
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByDesc(Report::getCreateTime);
        
        // 执行查询并返回列表
        return list(wrapper);
    }

    @Override
    public List<ReportVo> getReportVoList(String type, String department) {
        // 调用Mapper的查询方法
        return baseMapper.selectReportVoPage(type, department);
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