package com.insightflow.service.task;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.dto.TaskTargetDTO;
import java.math.BigDecimal;
import java.util.List;

public interface TaskTargetService extends IService<TaskTarget> {
    List<TaskTargetDTO> getTaskTargetList(Long executorId,String taskName);
    
    List<TaskTargetDTO> getTaskTargetAchievement(Long executorId,String taskName);
    
    boolean save(TaskTarget taskTarget);
    
    void delete(Long id);
    
    TaskTarget update(TaskTarget taskTarget);
    
    TaskTarget findById(Long id);
    
    void updateAchievedAmount(Long id, BigDecimal achievedAmount);
} 