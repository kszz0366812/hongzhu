package com.insightflow.service.statistics;

import com.insightflow.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface DataAnalysisService {
    /**
     * 获取区域销售额对比
     */
    List<RegionSalesComparisonDTO> getRegionSalesComparison(String regionLevel, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取区域商品销售分布
     */
    List<RegionProductDistributionDTO> getRegionProductDistribution(String regionLevel, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取区域商品销售统计
     */
    List<RegionProductSalesDTO> getRegionProductSales(String regionLevel, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取员工拜访达成率统计
     */
    List<EmployeeVisitRateDTO> getEmployeeVisitRate(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取员工销售业绩排名
     */
    List<EmployeeSalesRankDTO> getEmployeeSalesRank(String timeUnit, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取员工业绩详情
     */
    List<EmployeePerformanceDTO> getEmployeePerformance(String timeUnit, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取商品销售趋势
     */
    List<ProductSalesTrendDTO> getProductSalesTrend(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取商品销售详情
     */
    List<ProductSalesDetailDTO> getProductSalesDetail(LocalDateTime startTime, LocalDateTime endTime);
} 