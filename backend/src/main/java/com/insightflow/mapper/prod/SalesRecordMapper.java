package com.insightflow.mapper.prod;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SalesRecordMapper extends BaseMapper<SalesRecord> {
    List<RegionSalesComparisonDTO> getRegionSalesComparison(
        @Param("regionLevel") String regionLevel,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<RegionProductDistributionDTO> getRegionProductDistribution(
        @Param("regionLevel") String regionLevel,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<RegionProductSalesDTO> getRegionProductSales(
        @Param("regionLevel") String regionLevel,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<EmployeeSalesRankDTO> getEmployeeSalesRank(
        @Param("timeUnit") String timeUnit,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<EmployeePerformanceDTO> getEmployeePerformance(
        @Param("timeUnit") String timeUnit,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<ProductSalesTrendDTO> getProductSalesTrend(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<ProductSalesDetailDTO> getProductSalesDetail(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    List<SalesRecord> selectByParams(@Param("params") Map<String, Object> params);
    List<Map<String, Object>> selectProductAnalysis(@Param("params") Map<String, Object> params);
    List<Map<String, Object>> selectRegionAnalysis(@Param("params") Map<String, Object> params);
} 