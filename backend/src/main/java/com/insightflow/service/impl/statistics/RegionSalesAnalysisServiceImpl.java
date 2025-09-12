package com.insightflow.service.impl.statistics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insightflow.dto.RegionSalesQueryDTO;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.service.prod.SalesRecordService;
import com.insightflow.service.statistics.RegionSalesAnalysisService;
import com.insightflow.vo.RegionSalesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegionSalesAnalysisServiceImpl implements RegionSalesAnalysisService {

    @Autowired
    private SalesRecordService salesRecordService;

    @Override
    public List<RegionSalesVO> queryRegionSales(RegionSalesQueryDTO queryDTO) {
        // 1. 获取销售记录
        List<SalesRecord> salesRecords = getSalesRecordsByDateRange(queryDTO);
        
        // 2. 按区域分组统计
        Map<String, List<SalesRecord>> regionGroup = salesRecords.stream()
                .collect(Collectors.groupingBy(SalesRecord::getRegionLevel1));
        
        // 3. 计算每个区域的销售额和同比环比
        List<RegionSalesVO> result = new ArrayList<>();
        for (Map.Entry<String, List<SalesRecord>> entry : regionGroup.entrySet()) {
            RegionSalesVO vo = new RegionSalesVO();
            vo.setRegionName(entry.getKey());
            
            // 计算当前销售额
            BigDecimal currentAmount = entry.getValue().stream()
                    .map(SalesRecord::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setSalesAmount(currentAmount);
            
            // 计算同比（去年同期）
            BigDecimal lastYearAmount = calculateLastYearAmount(entry.getValue(), queryDTO);
            if (lastYearAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal yoy = currentAmount.subtract(lastYearAmount)
                        .divide(lastYearAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                vo.setYearOnYear(yoy);
            }
            
            // 计算环比（上月）
            BigDecimal lastMonthAmount = calculateLastMonthAmount(entry.getValue(), queryDTO);
            if (lastMonthAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal mom = currentAmount.subtract(lastMonthAmount)
                        .divide(lastMonthAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                vo.setMonthOnMonth(mom);
            }
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public List<RegionSalesVO> queryRegionProductDistribution(RegionSalesQueryDTO queryDTO) {
        // 1. 获取销售记录
        List<SalesRecord> salesRecords = getSalesRecordsByDateRange(queryDTO);
        
        // 2. 按区域和商品分组统计
        Map<String, Map<String, List<SalesRecord>>> regionProductGroup = salesRecords.stream()
                .collect(Collectors.groupingBy(SalesRecord::getRegionLevel1,
                        Collectors.groupingBy(SalesRecord::getProductName)));
        
        // 3. 计算每个区域各商品的销售占比
        List<RegionSalesVO> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<SalesRecord>>> regionEntry : regionProductGroup.entrySet()) {
            String regionName = regionEntry.getKey();
            Map<String, List<SalesRecord>> productGroup = regionEntry.getValue();
            
            // 计算区域总销售额
            BigDecimal regionTotal = productGroup.values().stream()
                    .flatMap(List::stream)
                    .map(SalesRecord::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算各商品占比
            for (Map.Entry<String, List<SalesRecord>> productEntry : productGroup.entrySet()) {
                RegionSalesVO vo = new RegionSalesVO();
                vo.setRegionName(regionName);
                vo.setProductName(productEntry.getKey());
                
                BigDecimal productAmount = productEntry.getValue().stream()
                        .map(SalesRecord::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setSalesAmount(productAmount);
                
                // 计算占比
                if (regionTotal.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentage = productAmount.divide(regionTotal, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
                    vo.setPercentage(percentage);
                }
                
                result.add(vo);
            }
        }
        
        return result;
    }

    @Override
    public List<RegionSalesVO> queryRegionSalesTrend(RegionSalesQueryDTO queryDTO) {
        // 1. 获取销售记录
        List<SalesRecord> salesRecords = getSalesRecordsByDateRange(queryDTO);
        
        // 2. 按时间维度分组统计
        Map<String, List<SalesRecord>> timeGroup = salesRecords.stream()
                .collect(Collectors.groupingBy(record -> {
                    LocalDateTime salesDate = record.getSalesDate();
                    switch (queryDTO.getTimeUnit()) {
                        case "day":
                            return salesDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        case "week":
                            return salesDate.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                        case "month":
                            return salesDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        default:
                            return salesDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                }));
        
        // 3. 计算每个时间点的销售额和趋势
        List<RegionSalesVO> result = new ArrayList<>();
        for (Map.Entry<String, List<SalesRecord>> entry : timeGroup.entrySet()) {
            RegionSalesVO vo = new RegionSalesVO();
            vo.setTimePoint(entry.getKey());
            
            BigDecimal amount = entry.getValue().stream()
                    .map(SalesRecord::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setSalesAmount(amount);
            
            result.add(vo);
        }
        
        // 4. 按时间排序
        result.sort(Comparator.comparing(RegionSalesVO::getTimePoint));
        
        return result;
    }

    private List<SalesRecord> getSalesRecordsByDateRange(RegionSalesQueryDTO queryDTO) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, queryDTO.getStartDate(), queryDTO.getEndDate());
        
        if (queryDTO.getRegionLevel() != null) {
            switch (queryDTO.getRegionLevel()) {
                case 1:
                    wrapper.eq(SalesRecord::getRegionLevel1, queryDTO.getRegionName());
                    break;
                case 2:
                    wrapper.eq(SalesRecord::getRegionLevel2, queryDTO.getRegionName());
                    break;
                case 3:
                    wrapper.eq(SalesRecord::getRegionLevel3, queryDTO.getRegionName());
                    break;
            }
        }
        
        return salesRecordService.list(wrapper);
    }

    private BigDecimal calculateLastYearAmount(List<SalesRecord> currentRecords, RegionSalesQueryDTO queryDTO) {
        LocalDate startDate = queryDTO.getStartDate().toLocalDate().minusYears(1);
        LocalDate endDate = queryDTO.getEndDate().toLocalDate().minusYears(1);
        
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, startDate, endDate);
        
        List<SalesRecord> lastYearRecords = salesRecordService.list(wrapper);
        return lastYearRecords.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateLastMonthAmount(List<SalesRecord> currentRecords, RegionSalesQueryDTO queryDTO) {
        LocalDate startDate = queryDTO.getStartDate().toLocalDate().minusMonths(1);
        LocalDate endDate = queryDTO.getEndDate().toLocalDate().minusMonths(1);
        
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, startDate, endDate);
        
        List<SalesRecord> lastMonthRecords = salesRecordService.list(wrapper);
        return lastMonthRecords.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 