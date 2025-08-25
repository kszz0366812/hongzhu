package com.insightflow.controller.report;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insightflow.dto.ReportDTO;
import com.insightflow.service.report.ReportService;
import com.insightflow.common.util.Result;
import com.insightflow.vo.ReportVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报告管理")
@RestController
    @RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "获取报告列表")
    @RequestMapping("/list")
    public Result<IPage<ReportVo>> getReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String department) {
        IPage<ReportVo> reportList = reportService.getReportVoList(page, size, type, department);
        return Result.success(reportList);
    }

    @Operation(summary = "获取报告统计信息")
    @RequestMapping("/statistics")
    public Result<Object> getReportStatistics() {
        Object statistics = reportService.getReportStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "创建报告")
    @RequestMapping("/create")
    public Result<Long> createReport(@RequestBody ReportDTO reportDTO) {
        Long reportId = reportService.createReport(reportDTO);
        return Result.success(reportId);
    }

    @Operation(summary = "更新报告")
    @RequestMapping("/update")
    public Result<Boolean> updateReport(@RequestBody ReportDTO reportDTO) {
       System.out.println("进入了更新方法");
        boolean success = reportService.updateReport(reportDTO);
        return Result.success(success);
    }

    @Operation(summary = "删除报告")
    @RequestMapping("/delete/{id}")
    public Result<Boolean> deleteReport(@PathVariable Long id) {
        boolean success = reportService.deleteReport(id);
        return Result.success(success);
    }
} 