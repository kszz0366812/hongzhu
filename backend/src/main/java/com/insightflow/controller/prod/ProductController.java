package com.insightflow.controller.prod;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.service.prod.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "商品管理")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取商品列表")
    @RequestMapping("/list")
    public Result<List<ProductInfo>> list(
            @RequestParam(value = "series", required = false) String series,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<ProductInfo> productList = productService.getProductList(series, keyword);
        return Result.success(productList);
    }

    @Operation(summary = "分页查询商品列表")
    @RequestMapping("/page")
    public Result<PageInfo<ProductInfo>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "商品名称") @RequestParam(required = false) String productName) {

        PageInfo<ProductInfo> page = productService.getProductPage(current, size, productName);
        return Result.success(page);
    }

    @Operation(summary = "获取商品详情")
    @RequestMapping("/getById/{id}")
    public Result<ProductInfo> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "新增商品")
    @RequestMapping("/save")
    public Result<Void> save(@RequestBody ProductInfo productInfo) {
        productService.save(productInfo);
        return Result.success();
    }

    @Operation(summary = "修改商品")
    @RequestMapping("/update")
    public Result<Void> update(@RequestBody ProductInfo productInfo) {
        productService.updateById(productInfo);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "获取商品销售统计")
    @RequestMapping("/salesStats")
    public Result<Map<String, Object>> getSalesStats(
            @Parameter(description = "商品ID") @RequestParam Long productId,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {
        
        Map<String, Object> stats = productService.getProductSalesStats(productId, startTime, endTime);
        return Result.success(stats);
    }

    @Operation(summary = "获取商品库存信息")
    @RequestMapping("/stockInfo")
    public Result<Map<String, Object>> getStockInfo(
            @Parameter(description = "商品ID") @RequestParam Long productId) {
        
        Map<String, Object> stockInfo = productService.getProductStockInfo(productId);
        return Result.success(stockInfo);
    }

    @Operation(summary = "更新商品价格")
    @RequestMapping("/updatePrice")
    public Result<Void> updatePrice(
            @Parameter(description = "商品ID") @RequestParam Long productId,
            @Parameter(description = "单价") @RequestParam Double unitPrice,
            @Parameter(description = "件价") @RequestParam Double casePrice) {
        
        boolean success = productService.updateProductPrice(productId, unitPrice, casePrice);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新价格失败");
        }
    }

    @Operation(summary = "获取商品系列列表")
    @RequestMapping("/seriesList")
    public Result<List<String>> getSeriesList() {
        List<String> seriesList = productService.getProductSeriesList();
        return Result.success(seriesList);
    }
}
