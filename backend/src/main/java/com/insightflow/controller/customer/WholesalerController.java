package com.insightflow.controller.customer;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.service.customer.WholesalerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "批发商管理")
@RestController
@RequestMapping("/wholesaler")
@RequiredArgsConstructor
public class WholesalerController {

    private final WholesalerService wholesalerService;

    @Operation(summary = "获取批发商列表")
    @RequestMapping("/list")
    public Result<List<WholesalerInfo>> getWholesalerList(
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<WholesalerInfo> wholesalerList = wholesalerService.getWholesalerList(level, keyword);
        return Result.success(wholesalerList);
    }

    @Operation(summary = "分页查询批发商列表")
    @RequestMapping("/page")
    public Result<PageInfo<WholesalerInfo>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "等级") @RequestParam(required = false) String level) {

        PageInfo<WholesalerInfo> page = wholesalerService.getWholesalerPage(current, size, keyword, level);
        return Result.success(page);
    }

    @Operation(summary = "获取批发商详情")
    @RequestMapping("getById/{id}")
    public Result<WholesalerInfo> getById(@PathVariable Long id) {
        return Result.success(wholesalerService.getById(id));
    }

    @Operation(summary = "新增批发商")
    @RequestMapping("/save")
    public Result<Void> save(@RequestBody WholesalerInfo wholesalerInfo) {
        wholesalerService.save(wholesalerInfo);
        return Result.success();
    }

    @Operation(summary = "修改批发商")
    @RequestMapping("/update")
    public Result<Void> update(@RequestBody WholesalerInfo wholesalerInfo) {
        wholesalerService.updateById(wholesalerInfo);
        return Result.success();
    }

    @Operation(summary = "删除批发商")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wholesalerService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "获取批发商等级列表")
    @RequestMapping("/levels")
    public Result<List<String>> getLevels() {
        List<String> levels = wholesalerService.getWholesalerLevelList();
        return Result.success(levels);
    }

    @Operation(summary = "获取批发商客户经理")
    @RequestMapping("/manager/{id}")
    public Result<Object> getManager(@PathVariable Long id) {
        Object manager = wholesalerService.getWholesalerManager(id);
        return Result.success(manager);
    }
}
