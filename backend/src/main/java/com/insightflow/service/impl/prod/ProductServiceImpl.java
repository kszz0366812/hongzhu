package com.insightflow.service.impl.prod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.mapper.prod.ProductInfoMapper;
import com.insightflow.service.prod.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductService {
    
    @Override
    public List<ProductInfo> getProductList(String series, String keyword) {
        LambdaQueryWrapper<ProductInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(series)) {
            wrapper.eq(ProductInfo::getSeries, series);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ProductInfo::getProductName, keyword)
                    .or()
                    .like(ProductInfo::getProductCode, keyword);
        }
        return list(wrapper);
    }
    
    @Override
    public Map<String, Object> getProductSalesStats(Long productId, String startTime, String endTime) {
        // TODO: 实现商品销售统计
        return new HashMap<>();
    }
    
    @Override
    public Map<String, Object> getProductStockInfo(Long productId) {
        // TODO: 实现商品库存信息查询
        return new HashMap<>();
    }
    
    @Override
    public boolean updateProductPrice(Long productId, Double unitPrice, Double casePrice) {
        ProductInfo product = getById(productId);
        if (product != null) {
            product.setUnitPrice(BigDecimal.valueOf(unitPrice));
            product.setCasePrice(BigDecimal.valueOf(casePrice));
            return updateById(product);
        }
        return false;
    }
    
    @Override
    public List<String> getProductSeriesList() {
        return list().stream()
                .map(ProductInfo::getSeries)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> batchImportProducts(List<ProductInfo> productList) {
        Map<String, Object> result = new HashMap<>();
        try {
            saveBatch(productList);
            result.put("success", true);
            result.put("message", "导入成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "导入失败：" + e.getMessage());
        }
        return result;
    }
} 