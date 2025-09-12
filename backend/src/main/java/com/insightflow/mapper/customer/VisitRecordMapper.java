package com.insightflow.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.dto.EmployeeVisitRateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VisitRecordMapper extends BaseMapper<VisitRecord> {
    
    List<EmployeeVisitRateDTO> getEmployeeVisitRate(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取拜访记录列表（包含终端名称、拜访人、是否成交等详细信息）
     * @param customerManager 客户经理
     * @param terminalCode 终端编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isDeal 是否成交
     * @return 拜访记录列表
     */
    List<VisitRecord> getVisitRecordListWithDetails(
        @Param("customerManager") String customerManager,
        @Param("terminalCode") String terminalCode,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("isDeal") Integer isDeal
    );
} 