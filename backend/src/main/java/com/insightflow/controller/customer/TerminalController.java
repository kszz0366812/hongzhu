package com.insightflow.controller.customer;

import com.insightflow.common.Result;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.service.customer.TerminalInfoService;
import io.swagger.v3.oas.annotations.Operation;
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
    public Result<List<TerminalInfo>> list() {
        return Result.success(terminalInfoService.list());
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
    @RequestMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        terminalInfoService.removeById(id);
        return Result.success();
    }
} 