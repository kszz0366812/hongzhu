package com.insightflow.controller.prod;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.entity.prod.DealRecord;
import com.insightflow.service.prod.DealRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "成交记录管理")
@RestController
@RequestMapping("/deal-record")
@RequiredArgsConstructor
public class DealRecordController {

    private final DealRecordService dealRecordService;

    @Operation(summary = "获取成交记录列表")
    @RequestMapping("/list")
    public Result<List<DealRecord>> getDealRecordList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "salesDateTime", required = false) LocalDateTime salesDateTime,
            @RequestParam(value = "customerManager", required = false) String customerManager) {
        List<DealRecord> dealRecordList = dealRecordService.getDealRecordList(keyword, salesDateTime, customerManager);
        return Result.success(dealRecordList);
    }

    @Operation(summary = "分页查询成交记录列表")
    @RequestMapping("/page")
    public Result<PageInfo<DealRecord>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "销售时间") @RequestParam(required = false) LocalDateTime salesDateTime,
            @Parameter(description = "客户经理") @RequestParam(required = false) String customerManager) {

        PageInfo<DealRecord> page = dealRecordService.getDealRecordPage(current, size, keyword, salesDateTime, customerManager);
        return Result.success(page);
    }

    @Operation(summary = "获取成交记录详情")
    @RequestMapping("getById/{id}")
    public Result<DealRecord> getById(@PathVariable Long id) {
        return Result.success(dealRecordService.getById(id));
    }

    @Operation(summary = "新增成交记录")
    @RequestMapping("/save")
    public Result<Void> save(@RequestBody DealRecord dealRecord) {
        dealRecordService.save(dealRecord);
        return Result.success();
    }

    @Operation(summary = "修改成交记录")
    @RequestMapping("/update")
    public Result<Void> update(@RequestBody DealRecord dealRecord) {
        dealRecordService.updateById(dealRecord);
        return Result.success();
    }

    @Operation(summary = "删除成交记录")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dealRecordService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "获取成交记录统计")
    @RequestMapping("/stats")
    public Result<Map<String, Object>> getStats(
            @Parameter(description = "开始时间") @RequestParam(required = false) LocalDateTime startDateTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) LocalDateTime endDateTime,
            @Parameter(description = "客户经理") @RequestParam(required = false) String customerManager) {
        
        Map<String, Object> stats = dealRecordService.getDealRecordStats(startDateTime, endDateTime, customerManager);
        return Result.success(stats);
    }

}
