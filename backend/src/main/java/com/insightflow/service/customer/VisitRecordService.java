package com.insightflow.service.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.customer.VisitRecord;

import java.time.LocalDateTime;
import java.util.List;
import com.github.pagehelper.PageInfo;

public interface VisitRecordService extends IService<VisitRecord> {
    
    /**
     * 获取拜访记录列表
     * @param customerManager 客户经理
     * @param terminalCode 终端编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isDeal 是否成交
     * @return 拜访记录列表
     */
    List<VisitRecord> getVisitRecordList(String customerManager, String terminalCode, LocalDateTime startTime, LocalDateTime endTime, Integer isDeal);
    
    /**
     * 分页查询拜访记录列表
     * @param current 当前页
     * @param size 每页大小
     * @param customerManager 客户经理
     * @param terminalCode 终端编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isDeal 是否成交
     * @return 分页结果
     */
    PageInfo<VisitRecord> getVisitRecordPage(Integer current, Integer size, String customerManager, String terminalCode, LocalDateTime startTime, LocalDateTime endTime, Integer isDeal);
} 