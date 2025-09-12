package com.insightflow.service.impl.statistics;

import com.insightflow.dto.*;
import com.insightflow.mapper.prod.SalesRecordMapper;
import com.insightflow.mapper.customer.VisitRecordMapper;
import com.insightflow.service.statistics.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private SalesRecordMapper salesRecordMapper;

    @Autowired
    private VisitRecordMapper visitRecordMapper;

    @Override
    public List<RegionSalesComparisonDTO> getRegionSalesComparison(String regionLevel, LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getRegionSalesComparison(regionLevel, startTime, endTime);
    }

    @Override
    public List<RegionProductDistributionDTO> getRegionProductDistribution(String regionLevel, LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getRegionProductDistribution(regionLevel, startTime, endTime);
    }

    @Override
    public List<RegionProductSalesDTO> getRegionProductSales(String regionLevel, LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getRegionProductSales(regionLevel, startTime, endTime);
    }

    @Override
    public List<EmployeeVisitRateDTO> getEmployeeVisitRate(LocalDateTime startTime, LocalDateTime endTime) {
        return visitRecordMapper.getEmployeeVisitRate(startTime, endTime);
    }

    @Override
    public List<EmployeeSalesRankDTO> getEmployeeSalesRank(String timeUnit, LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getEmployeeSalesRank(timeUnit, startTime, endTime);
    }

    @Override
    public List<EmployeePerformanceDTO> getEmployeePerformance(String timeUnit, LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getEmployeePerformance(timeUnit, startTime, endTime);
    }

    @Override
    public List<ProductSalesTrendDTO> getProductSalesTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getProductSalesTrend(startTime, endTime);
    }

    @Override
    public List<ProductSalesDetailDTO> getProductSalesDetail(LocalDateTime startTime, LocalDateTime endTime) {
        return salesRecordMapper.getProductSalesDetail(startTime, endTime);
    }
} 