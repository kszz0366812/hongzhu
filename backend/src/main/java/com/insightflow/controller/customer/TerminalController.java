package com.insightflow.controller.customer;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.service.customer.TerminalInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "终端管理")
@RestController
@RequestMapping("/terminal")
@RequiredArgsConstructor
public class TerminalController {

    private final TerminalInfoService terminalInfoService;

    @Operation(summary = "获取终端列表")
    @RequestMapping("/list")
    public Result<List<TerminalInfo>> getTerminalList(
            @RequestParam(value = "terminal_name", required = false) String terminalName,
            @RequestParam(value = "terminal_type", required = false) String terminalType,
            @RequestParam(value = "customer_manager", required = false) String customerManager) {
        List<TerminalInfo> terminalList = terminalInfoService.getTerminalList(terminalName, terminalType, customerManager);
        return Result.success(terminalList);
    }

    @Operation(summary = "分页查询终端列表")
    @RequestMapping("/page")
    public Result<PageInfo<TerminalInfo>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "是否排线") @RequestParam(required = false) Integer isScheduled) {

        PageInfo<TerminalInfo> page = terminalInfoService.getTerminalPage(current, size, keyword, isScheduled);
        return Result.success(page);
    }

    @Operation(summary = "获取终端详情")
    @RequestMapping("getById/{id}")
    public Result<TerminalInfo> getById(@PathVariable Long id) {
        return Result.success(terminalInfoService.getById(id));
    }

    @Operation(summary = "新增终端")
    @RequestMapping("/save")
    public Result<Void> save(@RequestBody TerminalInfo terminalInfo) {
        terminalInfoService.save(terminalInfo);
        return Result.success();
    }

    @Operation(summary = "修改终端")
    @RequestMapping("/update")
    public Result<Void> update(@RequestBody TerminalInfo terminalInfo) {
        terminalInfoService.updateById(terminalInfo);
        return Result.success();
    }

    @Operation(summary = "删除终端")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        terminalInfoService.removeById(id);
        return Result.success();
    }
} 