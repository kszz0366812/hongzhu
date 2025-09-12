package com.insightflow.mapper.task;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.dto.TaskTargetDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TaskTargetMapper extends BaseMapper<TaskTarget> {
    List<TaskTargetDTO> getTaskTargetList(
        @Param("executorId") Long executorId,
        @Param("taskName") String taskName
    );
    
    List<TaskTargetDTO> getTaskTargetAchievement(
        @Param("executorId") Long executorId,
        @Param("taskName") String taskName
    );
    
    void updateAchievedAmount(
        @Param("id") Long id,
        @Param("achievedAmount") BigDecimal achievedAmount
    );
} 