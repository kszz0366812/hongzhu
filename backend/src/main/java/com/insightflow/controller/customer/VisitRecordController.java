package com.insightflow.controller.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.service.customer.VisitRecordService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "拜访记录管理")
@RestController
    @RequestMapping("/visit")
public class VisitRecordController {

    @Autowired
    private VisitRecordService visitRecordService;

    @Operation(summary = "分页查询拜访记录列表")
    @RequestMapping("/page")
    public Result<Page<VisitRecord>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String visitorName,
            @RequestParam(required = false) String terminalName,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        
        Page<VisitRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<VisitRecord>()
                .like(visitorName != null, VisitRecord::getVisitorName, visitorName)
                .like(terminalName != null, VisitRecord::getTerminalName, terminalName)
                .ge(startTime != null, VisitRecord::getVisitTime, startTime)
                .le(endTime != null, VisitRecord::getVisitTime, endTime)
                .orderByDesc(VisitRecord::getCreateTime);
        
        return Result.success(visitRecordService.page(page, wrapper));
    }

    @Operation(summary = "新增拜访记录")
    @RequestMapping("/save")
    public Result<Boolean> save(@RequestBody VisitRecord visitRecord) {
        return Result.success(visitRecordService.save(visitRecord));
    }

    @Operation(summary = "修改拜访记录")
    @RequestMapping("/update")
    public Result<Boolean> update(@RequestBody VisitRecord visitRecord) {
        return Result.success(visitRecordService.updateById(visitRecord));
    }

    @Operation(summary = "删除拜访记录")
    @RequestMapping("/{delete}/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(visitRecordService.removeById(id));
    }

    @Operation(summary = "获取拜访记录详情")
    @RequestMapping("/getById/{id}")
    public Result<VisitRecord> getById(@PathVariable Long id) {
        return Result.success(visitRecordService.getById(id));
    }
} 