package com.insightflow.service.prod;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * 批量导入商品信息
     * @param productList 商品列表
     * @return 导入结果
     */
    Map<String, Object> batchImportProducts(List<ProductInfo> productList);
} 