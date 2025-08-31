package com.insightflow.service.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.customer.VisitRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VisitRecordService extends IService<VisitRecord> {
    
    /**
     * 获取员工拜访统计
     * @param employeeId 员工ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 拜访统计信息
     */
    Map<String, Object> getEmployeeVisitStats(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取终端拜访记录
     * @param terminalId 终端ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 拜访记录列表
     */
    List<VisitRecord> getTerminalVisitRecords(Long terminalId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取区域拜访统计
     * @param regionCode 区域编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 区域拜访统计信息
     */
    Map<String, Object> getRegionVisitStats(String regionCode, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 创建拜访记录
     * @param visitRecord 拜访记录信息
     * @return 是否创建成功
     */
    boolean createVisitRecord(VisitRecord visitRecord);
    
    /**
     * 更新拜访记录
     * @param visitRecord 拜访记录信息
     * @return 是否更新成功
     */
    boolean updateVisitRecord(VisitRecord visitRecord);
    
    /**
     * 获取拜访达成率
     * @param employeeId 员工ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 拜访达成率
     */
    Double getVisitCompletionRate(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
} 