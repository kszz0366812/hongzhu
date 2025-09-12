package com.insightflow.controller.statistics;

import com.insightflow.common.Result;
import com.insightflow.dto.*;
import com.insightflow.service.statistics.DataAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "数据分析接口")
@RestController
@RequestMapping("analysis")
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Operation(summary = "获取区域销售额对比")
    @RequestMapping("/region/sales/comparison")
    public Result<List<RegionSalesComparisonDTO>> getRegionSalesComparison(
            @Parameter(description = "区域级别") @RequestParam String regionLevel,
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取区域销售额对比 - 区域级别: {}, 开始时间: {}, 结束时间: {}", regionLevel, startTime, endTime);
        try {
            List<RegionSalesComparisonDTO> result = dataAnalysisService.getRegionSalesComparison(regionLevel, startTime, endTime);
            log.info("获取区域销售额对比成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取区域销售额对比失败", e);
            return Result.error("获取区域销售额对比失败");
        }
    }

    @Operation(summary = "获取区域商品销售分布")
    @RequestMapping("/region/product/distribution")
    public Result<List<RegionProductDistributionDTO>> getRegionProductDistribution(
            @Parameter(description = "区域级别") @RequestParam String regionLevel,
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取区域商品销售分布 - 区域级别: {}, 开始时间: {}, 结束时间: {}", regionLevel, startTime, endTime);
        try {
            List<RegionProductDistributionDTO> result = dataAnalysisService.getRegionProductDistribution(regionLevel, startTime, endTime);
            log.info("获取区域商品销售分布成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取区域商品销售分布失败", e);
            return Result.error("获取区域商品销售分布失败");
        }
    }

    @Operation(summary = "获取区域商品销售统计")
    @RequestMapping("/region/product/sales")
    public Result<List<RegionProductSalesDTO>> getRegionProductSales(
            @Parameter(description = "区域级别") @RequestParam String regionLevel,
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取区域商品销售统计 - 区域级别: {}, 开始时间: {}, 结束时间: {}", regionLevel, startTime, endTime);
        try {
            List<RegionProductSalesDTO> result = dataAnalysisService.getRegionProductSales(regionLevel, startTime, endTime);
            log.info("获取区域商品销售统计成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取区域商品销售统计失败", e);
            return Result.error("获取区域商品销售统计失败");
        }
    }

    @Operation(summary = "获取员工拜访达成率统计")
    @RequestMapping("/employee/visit/rate")
    public Result<List<EmployeeVisitRateDTO>> getEmployeeVisitRate(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取员工拜访达成率统计 - 开始时间: {}, 结束时间: {}", startTime, endTime);
        try {
            List<EmployeeVisitRateDTO> result = dataAnalysisService.getEmployeeVisitRate(startTime, endTime);
            log.info("获取员工拜访达成率统计成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取员工拜访达成率统计失败", e);
            return Result.error("获取员工拜访达成率统计失败");
        }
    }

    @Operation(summary = "获取员工销售业绩排名")
    @RequestMapping("/employee/sales/rank")
    public Result<List<EmployeeSalesRankDTO>> getEmployeeSalesRank(
            @Parameter(description = "时间单位") @RequestParam String timeUnit,
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取员工销售业绩排名 - 时间单位: {}, 开始时间: {}, 结束时间: {}", timeUnit, startTime, endTime);
        try {
            List<EmployeeSalesRankDTO> result = dataAnalysisService.getEmployeeSalesRank(timeUnit, startTime, endTime);
            log.info("获取员工销售业绩排名成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取员工销售业绩排名失败", e);
            return Result.error("获取员工销售业绩排名失败");
        }
    }

    @Operation(summary = "获取员工业绩详情")
    @RequestMapping("/employee/performance")
    public Result<List<EmployeePerformanceDTO>> getEmployeePerformance(
            @Parameter(description = "时间单位") @RequestParam String timeUnit,
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取员工业绩详情 - 时间单位: {}, 开始时间: {}, 结束时间: {}", timeUnit, startTime, endTime);
        try {
            List<EmployeePerformanceDTO> result = dataAnalysisService.getEmployeePerformance(timeUnit, startTime, endTime);
            log.info("获取员工业绩详情成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取员工业绩详情失败", e);
            return Result.error("获取员工业绩详情失败");
        }
    }

    @Operation(summary = "获取商品销售趋势")
    @RequestMapping("/product/sales/trend")
    public Result<List<ProductSalesTrendDTO>> getProductSalesTrend(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取商品销售趋势 - 开始时间: {}, 结束时间: {}", startTime, endTime);
        try {
            List<ProductSalesTrendDTO> result = dataAnalysisService.getProductSalesTrend(startTime, endTime);
            log.info("获取商品销售趋势成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取商品销售趋势失败", e);
            return Result.error("获取商品销售趋势失败");
        }
    }

    @Operation(summary = "获取商品销售详情")
    @RequestMapping("/product/sales/detail")
    public Result<List<ProductSalesDetailDTO>> getProductSalesDetail(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取商品销售详情 - 开始时间: {}, 结束时间: {}", startTime, endTime);
        try {
            List<ProductSalesDetailDTO> result = dataAnalysisService.getProductSalesDetail(startTime, endTime);
            log.info("获取商品销售详情成功 - 数据条数: {}", result.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取商品销售详情失败", e);
            return Result.error("获取商品销售详情失败");
        }
    }
} 