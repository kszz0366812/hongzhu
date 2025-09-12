package com.insightflow.service.impl.prod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.mapper.prod.ProductInfoMapper;
import com.insightflow.service.prod.ProductService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ProductInfo productInfo) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(productInfo);
        return super.save(productInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<ProductInfo> productInfoList) {
        // 批量设置创建信息
        productInfoList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(productInfoList);
    }
    
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
    public PageInfo<ProductInfo> getProductPage(Integer current, Integer size, String productName) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<ProductInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(productName)) {
            wrapper.like(ProductInfo::getProductName, productName);
        }
        
        wrapper.orderByDesc(ProductInfo::getCreateTime);
        
        // 执行查询
        List<ProductInfo> list = list(wrapper);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
} 