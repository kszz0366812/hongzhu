package com.insightflow.service.statistics;

import com.insightflow.dto.RegionSalesComparisonDTO;
import com.insightflow.dto.RegionProductDistributionDTO;
import com.insightflow.dto.RegionProductSalesDTO;

import java.time.LocalDate;
import java.util.List;

public interface RegionAnalysisService {
    
    /**
     * 获取区域销售额对比数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param regionLevel 区域级别
     * @return 区域销售额对比数据列表
     */
    List<RegionSalesComparisonDTO> getRegionSalesComparison(LocalDate startDate, LocalDate endDate, Integer regionLevel);
    
    /**
     * 获取区域商品销售分布数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param regionCode 区域编码
     * @return 区域商品销售分布数据列表
     */
    List<RegionProductDistributionDTO> getRegionProductDistribution(LocalDate startDate, LocalDate endDate, String regionCode);
    
    /**
     * 获取区域商品销售统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param regionCode 区域编码
     * @return 区域商品销售统计数据列表
     */
    List<RegionProductSalesDTO> getRegionProductSales(LocalDate startDate, LocalDate endDate, String regionCode);
} 