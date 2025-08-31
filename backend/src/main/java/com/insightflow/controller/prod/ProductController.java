package com.insightflow.controller.prod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.service.prod.ProductService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "产品管理")
@RestController
    @RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "分页查询产品列表")
    @RequestMapping("/page")
    public Result<Page<ProductInfo>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String productName) {
        
        Page<ProductInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<ProductInfo> wrapper = new LambdaQueryWrapper<ProductInfo>()
                .like(productName != null, ProductInfo::getProductName, productName)
                .orderByDesc(ProductInfo::getCreateTime);
        
        return Result.success(productService.page(page, wrapper));
    }

    @Operation(summary = "新增产品")
    @RequestMapping("save")
    public Result<Boolean> save(@RequestBody ProductInfo product) {
        return Result.success(productService.save(product));
    }

    @Operation(summary = "修改产品")
    @RequestMapping
    public Result<Boolean> update(@RequestBody ProductInfo product) {
        return Result.success(productService.updateById(product));
    }

    @Operation(summary = "删除产品")
    @RequestMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(productService.removeById(id));
    }

    @Operation(summary = "获取产品详情")
    @RequestMapping("getById/{id}")
    public Result<ProductInfo> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }
} 