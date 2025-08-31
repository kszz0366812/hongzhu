package com.insightflow.service.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.customer.WholesalerInfo;

import java.util.List;
import java.util.Map;

public interface WholesalerService extends IService<WholesalerInfo> {
    
    /**
     * 获取批发商列表
     * @param level 等级
     * @param keyword 关键词
     * @return 批发商列表
     */
    List<WholesalerInfo> getWholesalerList(String level, String keyword);
    
    /**
     * 获取批发商销售统计
     * @param wholesalerId 批发商ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售统计数据
     */
    Map<String, Object> getWholesalerSalesStats(Long wholesalerId, String startTime, String endTime);
    
    /**
     * 获取批发商等级列表
     * @return 等级列表
     */
    List<String> getWholesalerLevelList();
    
    /**
     * 更新批发商信息
     * @param wholesalerInfo 批发商信息
     * @return 是否更新成功
     */
    boolean updateWholesalerInfo(WholesalerInfo wholesalerInfo);
    
    /**
     * 获取批发商客户经理
     * @param wholesalerId 批发商ID
     * @return 客户经理信息
     */
    Map<String, Object> getWholesalerManager(Long wholesalerId);
    
    /**
     * 批量导入批发商信息
     * @param wholesalerList 批发商列表
     * @return 导入结果
     */
    Map<String, Object> batchImportWholesalers(List<WholesalerInfo> wholesalerList);
} 