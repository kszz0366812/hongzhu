package com.insightflow.controller.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.service.customer.WholesalerService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "批发商管理")
@RestController
    @RequestMapping("/wholesaler")
public class WholesalerController {

    @Autowired
    private WholesalerService wholesalerService;

    @Operation(summary = "分页查询批发商列表")
    @RequestMapping("/page")
    public Result<Page<WholesalerInfo>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String dealerName,
            @RequestParam(required = false) String level) {
        
        Page<WholesalerInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<WholesalerInfo> wrapper = new LambdaQueryWrapper<WholesalerInfo>()
                .like(dealerName != null, WholesalerInfo::getDealerName, dealerName)
                .eq(level != null, WholesalerInfo::getLevel, level)
                .orderByDesc(WholesalerInfo::getCreateTime);
        
        return Result.success(wholesalerService.page(page, wrapper));
    }

    @Operation(summary = "新增批发商")
    @RequestMapping("/save")
    public Result<Boolean> save(@RequestBody WholesalerInfo wholesaler) {
        return Result.success(wholesalerService.save(wholesaler));
    }

    @Operation(summary = "修改批发商")
    @RequestMapping
    public Result<Boolean> update(@RequestBody WholesalerInfo wholesaler) {
        return Result.success(wholesalerService.updateById(wholesaler));
    }

    @Operation(summary = "删除批发商")
    @RequestMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(wholesalerService.removeById(id));
    }

    @Operation(summary = "获取批发商详情")
    @RequestMapping("getById/{id}")
    public Result<WholesalerInfo> getById(@PathVariable Long id) {
        return Result.success(wholesalerService.getById(id));
    }
} 