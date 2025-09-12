package com.insightflow.service.prod;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.entity.prod.ProductInfo;

import java.util.List;
import java.util.Map;

public interface ProductService extends IService<ProductInfo> {
    
    /**
     * 获取商品列表
     * @param series 系列
     * @param keyword 关键词
     * @return 商品列表
     */
    List<ProductInfo> getProductList(String series, String keyword);
    
    /**
     * 获取商品销售统计
     * @param productId 商品ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售统计数据
     */
    Map<String, Object> getProductSalesStats(Long productId, String startTime, String endTime);
    
    /**
     * 获取商品库存信息
     * @param productId 商品ID
     * @return 库存信息
     */
    Map<String, Object> getProductStockInfo(Long productId);
    
    /**
     * 更新商品价格
     * @param productId 商品ID
     * @param unitPrice 单价
     * @param casePrice 件价
     * @return 是否更新成功
     */
    boolean updateProductPrice(Long productId, Double unitPrice, Double casePrice);
    
    /**
     * 获取商品系列列表
     * @return 系列列表
     */
    List<String> getProductSeriesList();
    
    /**
     * 分页查询商品列表
     * @param current 当前页
     * @param size 每页大小
     * @param productName 商品名称
     * @return 分页结果
     */
    PageInfo<ProductInfo> getProductPage(Integer current, Integer size, String productName);
    
    /**
     * 批量保存商品信息
     * @param productInfoList 商品信息列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<ProductInfo> productInfoList);
} 