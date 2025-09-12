package com.insightflow.service.impl.statistics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insightflow.dto.RegionSalesComparisonDTO;
import com.insightflow.dto.RegionProductDistributionDTO;
import com.insightflow.dto.RegionProductSalesDTO;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.service.prod.SalesRecordService;
import com.insightflow.service.statistics.RegionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegionAnalysisServiceImpl implements RegionAnalysisService {

    @Autowired
    private SalesRecordService salesRecordService;

    @Override
    public List<RegionSalesComparisonDTO> getRegionSalesComparison(LocalDate startDate, LocalDate endDate, Integer regionLevel) {
        // 1. 获取销售记录
        List<SalesRecord> salesRecords = getSalesRecordsByDateRange(startDate, endDate);
        
        // 2. 按区域分组统计
        Map<String, List<SalesRecord>> regionGroup = salesRecords.stream()
                .collect(Collectors.groupingBy(record -> {
                    switch (regionLevel) {
                        case 1: return record.getRegionLevel1();
                        case 2: return record.getRegionLevel2();
                        case 3: return record.getRegionLevel3();
                        default: return record.getRegionLevel1();
                    }
                }));
        
        // 3. 计算每个区域的销售额和同比环比
        List<RegionSalesComparisonDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<SalesRecord>> entry : regionGroup.entrySet()) {
            RegionSalesComparisonDTO dto = new RegionSalesComparisonDTO();
            dto.setRegionName(entry.getKey());
            
            // 计算当前销售额
            BigDecimal currentAmount = entry.getValue().stream()
                    .map(SalesRecord::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setSalesAmount(currentAmount);
            
            // 计算同比（去年同期）
            BigDecimal lastYearAmount = calculateLastYearAmount(entry.getValue(), startDate, endDate);
            if (lastYearAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal yoy = currentAmount.subtract(lastYearAmount)
                        .divide(lastYearAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                dto.setYearOnYear(yoy);
            }
            
            // 计算环比（上月）
            BigDecimal lastMonthAmount = calculateLastMonthAmount(entry.getValue(), startDate, endDate);
            if (lastMonthAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal mom = currentAmount.subtract(lastMonthAmount)
                        .divide(lastMonthAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                dto.setMonthOnMonth(mom);
            }
            
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public List<RegionProductDistributionDTO> getRegionProductDistribution(LocalDate startDate, LocalDate endDate, String regionCode) {
        // 1. 获取销售记录
        List<SalesRecord> salesRecords = getSalesRecordsByDateRange(startDate, endDate, regionCode);
        
        // 2. 按商品分组统计
        Map<String, List<SalesRecord>> productGroup = salesRecords.stream()
                .collect(Collectors.groupingBy(SalesRecord::getProductName));
        
        // 3. 计算每个商品的销售占比
        List<RegionProductDistributionDTO> result = new ArrayList<>();
        BigDecimal totalAmount = salesRecords.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        for (Map.Entry<String, List<SalesRecord>> entry : productGroup.entrySet()) {
            RegionProductDistributionDTO dto = new RegionProductDistributionDTO();
            dto.setProductName(entry.getKey());
            
            BigDecimal productAmount = entry.getValue().stream()
                    .map(SalesRecord::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setSalesAmount(productAmount);
            
            // 计算占比
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = productAmount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                dto.setPercentage(percentage);
            }
            
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public List<RegionProductSalesDTO> getRegionProductSales(LocalDate startDate, LocalDate endDate, String regionCode) {
        // 1. 获取销售记录
        List<SalesRecord> salesRecords = getSalesRecordsByDateRange(startDate, endDate, regionCode);
        
        // 2. 按商品分组统计
        Map<String, List<SalesRecord>> productGroup = salesRecords.stream()
                .collect(Collectors.groupingBy(SalesRecord::getProductName));
        
        // 3. 计算每个商品的销售情况
        List<RegionProductSalesDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<SalesRecord>> entry : productGroup.entrySet()) {
            RegionProductSalesDTO dto = new RegionProductSalesDTO();
            dto.setProductName(entry.getKey());
            
            List<SalesRecord> productSales = entry.getValue();
            dto.setSalesAmount(productSales.stream()
                    .map(SalesRecord::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            dto.setSalesQuantity(productSales.stream()
                    .mapToInt(SalesRecord::getQuantity)
                    .sum());
            
            dto.setSalesCount(productSales.size());
            
            result.add(dto);
        }
        
        return result;
    }

    private List<SalesRecord> getSalesRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return getSalesRecordsByDateRange(startDate, endDate, null);
    }

    private List<SalesRecord> getSalesRecordsByDateRange(LocalDate startDate, LocalDate endDate, String regionCode) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, 
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59));
        
        if (regionCode != null) {
            wrapper.eq(SalesRecord::getRegionLevel1, regionCode)
                    .or()
                    .eq(SalesRecord::getRegionLevel2, regionCode)
                    .or()
                    .eq(SalesRecord::getRegionLevel3, regionCode);
        }
        
        return salesRecordService.list(wrapper);
    }

    private BigDecimal calculateLastYearAmount(List<SalesRecord> currentRecords, LocalDate startDate, LocalDate endDate) {
        LocalDate lastYearStart = startDate.minusYears(1);
        LocalDate lastYearEnd = endDate.minusYears(1);
        
        List<SalesRecord> lastYearRecords = getSalesRecordsByDateRange(lastYearStart, lastYearEnd);
        return lastYearRecords.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateLastMonthAmount(List<SalesRecord> currentRecords, LocalDate startDate, LocalDate endDate) {
        LocalDate lastMonthStart = startDate.minusMonths(1);
        LocalDate lastMonthEnd = endDate.minusMonths(1);
        
        List<SalesRecord> lastMonthRecords = getSalesRecordsByDateRange(lastMonthStart, lastMonthEnd);
        return lastMonthRecords.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
