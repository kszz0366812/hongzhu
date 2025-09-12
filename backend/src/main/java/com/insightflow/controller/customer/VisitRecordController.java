package com.insightflow.controller.customer;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.service.customer.VisitRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "拜访记录管理")
@RestController
@RequestMapping("/visit-record")
@RequiredArgsConstructor
public class VisitRecordController {

    private final VisitRecordService visitRecordService;

    @Operation(summary = "获取拜访记录列表")
    @RequestMapping("/list")
    public Result<List<VisitRecord>> getVisitRecordList(
            @RequestParam(value = "customerManager", required = false) String customerManager,
            @RequestParam(value = "terminalCode", required = false) String terminalCode,
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime,
            @RequestParam(value = "isDeal", required = false) Integer isDeal) {
        List<VisitRecord> visitRecordList = visitRecordService.getVisitRecordList(customerManager, terminalCode, startTime, endTime, isDeal);
        return Result.success(visitRecordList);
    }

    @Operation(summary = "分页查询拜访记录列表")
    @RequestMapping("/page")
    public Result<PageInfo<VisitRecord>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "客户经理") @RequestParam(required = false) String customerManager,
            @Parameter(description = "终端编码") @RequestParam(required = false) String terminalCode,
            @Parameter(description = "开始时间") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) LocalDateTime endTime,
            @Parameter(description = "是否成交") @RequestParam(required = false) Integer isDeal) {

        PageInfo<VisitRecord> page = visitRecordService.getVisitRecordPage(current, size, customerManager, terminalCode, startTime, endTime, isDeal);
        return Result.success(page);
    }

    @Operation(summary = "获取拜访记录详情")
    @RequestMapping("getById/{id}")
    public Result<VisitRecord> getById(@PathVariable Long id) {
        return Result.success(visitRecordService.getById(id));
    }

    @Operation(summary = "新增拜访记录")
    @RequestMapping("/save")
    public Result<Void> save(@RequestBody VisitRecord visitRecord) {
        visitRecordService.save(visitRecord);
        return Result.success();
    }

    @Operation(summary = "修改拜访记录")
    @RequestMapping("/update")
    public Result<Void> update(@RequestBody VisitRecord visitRecord) {
        visitRecordService.updateById(visitRecord);
        return Result.success();
    }

    @Operation(summary = "删除拜访记录")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        visitRecordService.removeById(id);
        return Result.success();
    }

}
