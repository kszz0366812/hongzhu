package com.insightflow.service.prod;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.prod.DealRecord;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DealRecordService extends IService<DealRecord> {
    
    /**
     * 获取成交记录列表
     * @param keyword 关键词搜索
     * @param salesDateTime 销售时间
     * @param customerManager 客户经理
     * @return 成交记录列表
     */
    List<DealRecord> getDealRecordList(String keyword, LocalDateTime salesDateTime, String customerManager);
    
    /**
     * 分页查询成交记录
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词搜索
     * @param salesDateTime 销售时间
     * @param customerManager 客户经理
     * @return 分页结果
     */
    PageInfo<DealRecord> getDealRecordPage(Integer current, Integer size, String keyword, LocalDateTime salesDateTime, String customerManager);
    
    /**
     * 获取成交记录统计
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param customerManager 客户经理
     * @return 统计数据
     */
    Map<String, Object> getDealRecordStats(LocalDateTime startDateTime, LocalDateTime endDateTime, String customerManager);
    
    /**
     * 批量导入成交记录
     * @param dealRecordList 成交记录列表
     * @return 导入结果
     */
    boolean saveBatch(List<DealRecord> dealRecordList);
}
