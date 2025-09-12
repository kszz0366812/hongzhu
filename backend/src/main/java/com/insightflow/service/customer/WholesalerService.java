package com.insightflow.service.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
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
     * 批量保存批发商信息
     * @param wholesalerInfoList 批发商信息列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<WholesalerInfo> wholesalerInfoList);
    
    /**
     * 分页查询批发商列表
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词（批发商名称、编码、客户经理、联系人、联系电话）
     * @param level 等级
     * @return 分页结果
     */
    PageInfo<WholesalerInfo> getWholesalerPage(Integer current, Integer size, String keyword, String level);
} 