package com.insightflow.service.statistics;

import com.insightflow.dto.RegionSalesQueryDTO;
import com.insightflow.vo.RegionSalesVO;
import java.util.List;

public interface RegionSalesAnalysisService {
    List<RegionSalesVO> queryRegionSales(RegionSalesQueryDTO queryDTO);
    List<RegionSalesVO> queryRegionProductDistribution(RegionSalesQueryDTO queryDTO);
    List<RegionSalesVO> queryRegionSalesTrend(RegionSalesQueryDTO queryDTO);
} 