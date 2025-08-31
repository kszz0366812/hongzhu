package com.insightflow.controller.statistics;

import com.insightflow.dto.RegionSalesQueryDTO;
import com.insightflow.service.statistics.RegionSalesAnalysisService;
import com.insightflow.vo.RegionSalesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "区域销售分析", description = "提供区域维度销售数据分析接口")
@RestController
@RequestMapping("/analysis/region")
public class RegionSalesAnalysisController {

    @Autowired
    private RegionSalesAnalysisService regionSalesAnalysisService;

    @Operation(summary = "查询区域销售额", description = "按区域级别查询销售额数据")
    @RequestMapping("/sales")
    public ResponseEntity<List<RegionSalesVO>> queryRegionSales(RegionSalesQueryDTO queryDTO) {
        try {
            List<RegionSalesVO> result = regionSalesAnalysisService.queryRegionSales(queryDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询区域销售额失败", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "查询区域商品销售分布", description = "按区域查询商品销售分布数据")
    @RequestMapping("/product-distribution")
    public ResponseEntity<List<RegionSalesVO>> queryRegionProductDistribution(RegionSalesQueryDTO queryDTO) {
        try {
            List<RegionSalesVO> result = regionSalesAnalysisService.queryRegionProductDistribution(queryDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询区域商品销售分布失败", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "查询区域销售趋势", description = "按区域查询销售趋势数据")
    @RequestMapping("/sales-trend")
    public ResponseEntity<List<RegionSalesVO>> queryRegionSalesTrend(RegionSalesQueryDTO queryDTO) {
        try {
            List<RegionSalesVO> result = regionSalesAnalysisService.queryRegionSalesTrend(queryDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询区域销售趋势失败", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}